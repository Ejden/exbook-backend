package pl.exbook.exbook.image.adapter.mongodb

import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.Repository

interface MongoImageRepository : Repository<ImageDocument, String> {

    fun findById(id: String): ImageDocument?

    fun save(imageDocument: ImageDocument): ImageDocument

    fun removeById(id: String)
}

@Document("images")
data class ImageDocument(
    @Id
    val id: String? = null,
    val file: Binary,
    val contentType: ContentTypeDocument
)

data class ContentTypeDocument(
    val type: String,
    val subtype: String
)
