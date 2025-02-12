package ipar.plo9215

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.io.Serializable


// Clase Name
data class Name(
    val first: String = "",
    val last: String = ""
) : Serializable

// Clase User
data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val name: Name = Name(),
    val email: String = ""
) : Serializable {
    constructor() : this(ObjectId(), Name(), "")
}
