package ipar.plo9215

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set
import org.bson.Document
import org.bson.types.ObjectId
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.bson.codecs.pojo.annotations.BsonId
import java.io.Serializable
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.launch

@Composable
fun UserInterface() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var feeds by remember { mutableStateOf<List<RssFeed>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (!isLoggedIn) {
        LoginScreen(onLogin = { fetchedFeeds ->
            isLoggedIn = true
            feeds = fetchedFeeds
        })
    } else {

        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = {
                coroutineScope.launch {
                    feeds = MongoDB.listFeeds()
                }
            }) {
                Text("Refresh Feeds")
            }
            Spacer(modifier = Modifier.height(16.dp))
            AddFeedScreen(onAddFeed = { newFeed ->
                coroutineScope.launch {
                    MongoDB.insertFeed(newFeed)
                    feeds = MongoDB.listFeeds()
                }
            })
            Spacer(modifier = Modifier.height(16.dp))
            FeedListScreen(
                feeds = feeds,
                onDeleteFeed = { url ->
                    coroutineScope.launch {
                        MongoDB.deleteFeed(url)
                        feeds = MongoDB.listFeeds()
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                Database.deleteUser(MongoDB2.user)
                isLoggedIn = false
            }) {
                Text("Logout")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showDialog = true }) {
                Text("Delete the Database")
            }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirm Deletion") },
                    text = { Text("Are you sure that you want to delete all the database?") },
                    confirmButton = {
                        Button(onClick = {
                            coroutineScope.launch {
                                Database.deleteAllData()
                                MongoDB.deleteAllFeeds()
                                showDialog = false
                            }
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AddFeedScreen(onAddFeed: (RssFeed) -> Unit) {
    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Feed URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Feed Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (url.isNotEmpty() && title.isNotEmpty()) {
                    onAddFeed(RssFeed(url = url, title = title))
                    url = ""
                    title = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Feed")
        }
    }
}

@Composable
fun LoginScreen(onLogin: (List<RssFeed>) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var autoLogin by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val existingUser = Database.getUser(MongoDB2.user)
        if (existingUser != null) {
            username = existingUser.username
            password = existingUser.password
            autoLogin = true
        }
    }

    if (autoLogin) {
        coroutineScope.launch {
            val feeds = MongoDB.listFeeds()
            onLogin(feeds)
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (username == MongoDB2.user && password == MongoDB2.password) {
                        Database.saveUser(username, password)
                        coroutineScope.launch {
                            val feeds = MongoDB.listFeeds()
                            onLogin(feeds)
                        }
                    } else {
                        println("Credenciales incorrectas. Usuario: $username, Contraseña: $password")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }
    }
}
@Composable
fun FeedListScreen(feeds: List<RssFeed>, onDeleteFeed: (String) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(feeds) { feed ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = feed.title, style = MaterialTheme.typography.h6)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = feed.url, style = MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onDeleteFeed(feed.url) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Feed")
                    }
                }
            }
        }
    }
}
