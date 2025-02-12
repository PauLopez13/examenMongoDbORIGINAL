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
    private val pojoCodecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
        MongoClientSettings.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    )

    private val settings: MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString("mongodb+srv://acme:123@acme.ffwhp.mongodb.net/"))
        .codecRegistry(pojoCodecRegistry)
        .build()

    private val client: MongoClient = MongoClients.create(settings)
    private val database: MongoDatabase = client.getDatabase("Users")
    val userCollection: MongoCollection<User> = database.getCollection("testis", User::class.java)
}