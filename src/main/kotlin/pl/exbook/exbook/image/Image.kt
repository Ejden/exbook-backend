package pl.exbook.exbook.image

import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "images")
class Image (
    @Id
    var id: String?,
    val file: Binary
)