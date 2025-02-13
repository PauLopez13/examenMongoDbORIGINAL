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




@Composable
fun UserInterface() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var feeds by remember { mutableStateOf<List<RssFeed>>(emptyList()) }

    if (!isLoggedIn) {
        LoginScreen(onLogin = { isLoggedIn = true })
    } else {

        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { feeds = MongoDB.listFeeds() }) {
                Text("Refresh Feeds")
            }
            Spacer(modifier = Modifier.height(16.dp))
            AddFeedScreen(onAddFeed = { newFeed ->
                MongoDB.insertFeed(newFeed)
                feeds = MongoDB.listFeeds()
            })
            Spacer(modifier = Modifier.height(16.dp))
            FeedListScreen(
                feeds = feeds,
                onDeleteFeed = { url ->
                    MongoDB.deleteFeed(url)
                    feeds = MongoDB.listFeeds()
                }
            )
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
fun LoginScreen(onLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                    onLogin()
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
