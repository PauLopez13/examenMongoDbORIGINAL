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


@Composable
fun UserInterface() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var feeds by remember { mutableStateOf<List<RssFeed>>(emptyList()) }

    if (!isLoggedIn) {
        LoginScreen(onLogin = { isLoggedIn = true })
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { feeds = Database.getFeeds() }) {
                Text("Refresh Feeds")
            }
            Spacer(modifier = Modifier.height(16.dp))
            FeedListScreen(
                feeds = feeds,
                onDeleteFeed = { url ->
                    MongoDB.deleteFeed(url)
                    Database.deleteFeed(url)
                    feeds = feeds.filter { it.url != url }
                }
            )
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
                if (Database.getUser(username)?.password == password) {
                    onLogin()
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
