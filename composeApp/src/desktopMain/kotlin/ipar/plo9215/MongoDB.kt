package ipar.plo9215

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
import com.mongodb.MongoClientSettings
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.codecs.pojo.annotations.BsonId


object MongoDB {
    private const val cluster = "acme.ffwhp"
    private const val user = "acme"
    private const val password = "123"
    private const val url = "mongodb+srv://${user}:${password}@${cluster}.mongodb.net/?retryWrites=true&w=majority&appName=acme"

    private val pojoCodecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )

    private val settings: MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(url))
        .codecRegistry(pojoCodecRegistry)
        .build()

    private val client: MongoClient = MongoClients.create(settings)
    private val database: MongoDatabase = client.getDatabase("Users")

    val userCollection: MongoCollection<User> = database.getCollection("testis", User::class.java) // ACORDARSE DE BORRAR++++++++++++++++++++++++
    val rssCollection: MongoCollection<RssFeed> = database.getCollection("feeds", RssFeed::class.java)

    fun insertFeed(feed: RssFeed) {
        rssCollection.insertOne(feed)
    }

    fun listFeeds(): List<RssFeed> {
        return rssCollection.find().toList()
    }

    fun deleteFeed(url: String) {
        rssCollection.deleteOne(Document("url", url))
    }

    fun deleteAllFeeds() {
        rssCollection.deleteMany(Document())
    }
}
