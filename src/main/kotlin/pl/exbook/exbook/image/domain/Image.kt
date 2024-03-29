package pl.exbook.exbook.image.domain

import org.bson.types.Binary
import pl.exbook.exbook.shared.ImageId

class Image (
    var id: ImageId,
    val file: Binary,
    val contentType: ContentType
)

class ContentType(
    val type: String,
    val subtype: String
)
