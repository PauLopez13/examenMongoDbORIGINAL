package ipar.plo9215

import org.bson.types.ObjectId
import java.sql.Connection
import java.sql.DriverManager

object Database {
    private var connection: Connection? = null

    init {
        connection = DriverManager.getConnection("jdbc:sqlite:rssreader.db")
        createTables()
    }

    private fun createTables() {
        connection?.createStatement()?.execute(
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT
            )
            """
        )
        connection?.createStatement()?.execute(
            """
            CREATE TABLE IF NOT EXISTS feeds (
                id TEXT PRIMARY KEY,
                url TEXT,
                title TEXT
            )
            """
        )
    }

    fun saveUser(username: String, password: String): Boolean {
        val existingUser = connection?.prepareStatement(
            "SELECT * FROM users WHERE username = ?"
        )?.apply {
            setString(1, username)
        }?.executeQuery()?.next()

        return if (existingUser == true) {
            false // User already exists
        } else {
            connection?.prepareStatement(
                "INSERT INTO users (username, password) VALUES (?, ?)"
            )?.apply {
                setString(1, username)
                setString(2, password)
                executeUpdate()
            }
            true // User successfully inserted
        }
    }

    fun getUser(username: String): User? {
        return connection?.prepareStatement(
            "SELECT * FROM users WHERE username = ?"
        )?.apply {
            setString(1, username)
        }?.executeQuery()?.let {
            if (it.next()) {
                User(it.getString("username"), it.getString("password"))
            } else {
                null
            }
        }
    }

    fun saveFeed(feed: RssFeed) {
        connection?.prepareStatement(
            "INSERT INTO feeds (id, url, title) VALUES (?, ?, ?)"
        )?.apply {
            setString(1, feed.id.toString())
            setString(2, feed.url)
            setString(3, feed.title)
            executeUpdate()
        }
    }

    fun getFeeds(): List<RssFeed> {
        return connection?.prepareStatement(
            "SELECT * FROM feeds"
        )?.executeQuery()?.let {
            val feeds = mutableListOf<RssFeed>()
            while (it.next()) {
                feeds.add(
                    RssFeed(
                        ObjectId(it.getString("id")),
                        it.getString("url"),
                        it.getString("title")
                    )
                )
            }
            feeds
        } ?: emptyList()
    }

    fun deleteFeed(url: String) {
        connection?.prepareStatement(
            "DELETE FROM feeds WHERE url = ?"
        )?.apply {
            setString(1, url)
            executeUpdate()
        }
    }

    fun deleteUser(username: String) {
        connection?.prepareStatement(
            "DELETE FROM users WHERE username = ?"
        )?.apply {
            setString(1, username)
            executeUpdate()
        }
    }

    fun deleteAllData() {
        connection?.createStatement()?.execute("DELETE FROM users")
        connection?.createStatement()?.execute("DELETE FROM feeds")
    }
}



