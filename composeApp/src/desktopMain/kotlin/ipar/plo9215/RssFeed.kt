package ipar.plo9215

import org.bson.types.ObjectId
import org.bson.codecs.pojo.annotations.BsonId
import java.io.Serializable

data class RssFeed(
    @BsonId val id: ObjectId = ObjectId(),
    val url: String = "",
    val title: String = ""
) : Serializable