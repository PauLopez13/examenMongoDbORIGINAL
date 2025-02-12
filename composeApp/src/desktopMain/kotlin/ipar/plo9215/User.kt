package ipar.plo9215

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.io.Serializable


data class User(
    val username: String,
    val password: String
)