package ipar.plo9215

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.types.ObjectId

object MongoDB2 {
    const val user = "acme"
    const val password = "123"
    const val cluster = "acme.ffwhp"
}

object MongoDB {
    private const val user = MongoDB2.user
    private const val password = MongoDB2.password
    private const val url = "mongodb+srv://${user}:${password}@${MongoDB2.cluster}.mongodb.net/?retryWrites=true&w=majority&appName=acme"

    private val pojoCodecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )

    private val settings: MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(url))
        .codecRegistry(pojoCodecRegistry)
        .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
        .build()

    private val client = MongoClient.create(settings)
    private val database: MongoDatabase = client.getDatabase("Users")

    val rssCollection: MongoCollection<RssFeed> = database.getCollection("feeds")

    suspend fun insertFeed(feed: RssFeed) {
        try {
            rssCollection.insertOne(feed)
        } catch (e: Exception) {
            println("Error inserting feed: ${e.message}")
        }
    }

    suspend fun listFeeds(): List<RssFeed> {
        return try {
            rssCollection.find().toList()
        } catch (e: Exception) {
            println("Error listing feeds: ${e.message}")
            emptyList()
        }
    }

    suspend fun deleteFeed(url: String) {
        try {
            rssCollection.deleteOne(Filters.eq("url", url))
        } catch (e: Exception) {
            println("Error deleting feed: ${e.message}")
        }
    }

    suspend fun deleteAllFeeds() {
        try {
            rssCollection.deleteMany(Document())
        } catch (e: Exception) {
            println("Error deleting all feeds: ${e.message}")
        }
    }

    fun close() {
        client.close()
    }
}
