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


fun insertUser(user: User) {
    MongoDB.userCollection.insertOne(user)
}

fun listUsers(): List<User> {
    val users = MongoDB.userCollection.find().toList()
    users.forEach { user ->
        println("User: ${user.name.first} ${user.name.last}, Email: ${user.email}")
    }
    return users
}

fun updateUser(name: Name, newUser: User) {
    MongoDB.userCollection.updateOne(
        eq("name", Document("first", name.first).append("last", name.last)),
        set("name", Document("first", newUser.name.first).append("last", newUser.name.last))
    )
}

fun deleteUser(name: Name) {
    MongoDB.userCollection.deleteOne(eq("name", Document("first", name.first).append("last", name.last)))
}

@Composable
fun UserInterface() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var users by remember { mutableStateOf(listOf<User>()) }
    var showInsertForm by remember { mutableStateOf(false) }
    var showListForm by remember { mutableStateOf(false) }
    var showUpdateForm by remember { mutableStateOf(false) }
    var showDeleteForm by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        Text("User Management", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        // Insert User Section
        if (showInsertForm) {
            Column {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                Button(
                    onClick = {
                        val nameObj = Name(firstName, lastName)
                        val user = User(ObjectId(), nameObj, email)
                        insertUser(user)
                        users = listUsers()
                        showInsertForm = false
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Submit")
                }
            }
        } else {
            Button(onClick = { showInsertForm = true }, modifier = Modifier.padding(8.dp)) {
                Text("Insert")
            }
        }

        // List Users Section
        if (showListForm) {
            Column {
                Button(onClick = { users = listUsers() }, modifier = Modifier.padding(8.dp)) {
                    Text("Refresh List")
                }
                users.forEach { user ->
                    Text("NOMBRE: ${user.name.first} ${user.name.last}, Email: ${user.email}", modifier = Modifier.padding(8.dp))
                }
            }
        } else {
            Button(onClick = { showListForm = true }, modifier = Modifier.padding(8.dp)) {
                Text("List")
            }
        }

        // Update User Section
        if (showUpdateForm) {
            Column {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                Button(
                    onClick = {
                        val nameObj = Name(firstName, lastName)
                        val newUser = User(ObjectId(), nameObj, email)
                        updateUser(nameObj, newUser)
                        users = listUsers()
                        showUpdateForm = false
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Update")
                }
            }
        } else {
            Button(onClick = { showUpdateForm = true }, modifier = Modifier.padding(8.dp)) {
                Text("Update")
            }
        }

        // Delete User Section
        if (showDeleteForm) {
            Column {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                Button(
                    onClick = {
                        val nameObj = Name(firstName, lastName)
                        deleteUser(nameObj)
                        users = listUsers()
                        showDeleteForm = false
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Delete")
                }
            }
        } else {
            Button(onClick = { showDeleteForm = true }, modifier = Modifier.padding(8.dp)) {
                Text("Delete")
            }
        }
    }
}