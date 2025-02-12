package ipar.plo9215

import androidx.compose.foundation.layout.*
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


@Composable
fun RssFeedInterface() {
    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var feeds by remember { mutableStateOf(listOf<RssFeed>()) }

    LaunchedEffect(Unit) {
        feeds = MongoDB.listFeeds()
    }

    Column(Modifier.padding(16.dp)) {
        Text("RSS Feed Management", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Feed URL") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Feed Title") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        Button(
            onClick = {
                val newFeed = RssFeed(url = url, title = title)
                MongoDB.insertFeed(newFeed)
                feeds = MongoDB.listFeeds()
                url = ""
                title = ""
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Add Feed")
        }

        Text("Feeds List:", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
        feeds.forEach { feed ->
            Row(modifier = Modifier.padding(8.dp)) {
                Text("Title: ${feed.title}, URL: ${feed.url}")
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        MongoDB.deleteFeed(feed.url)
                        feeds = MongoDB.listFeeds()
                    }
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
