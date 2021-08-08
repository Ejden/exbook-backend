package pl.exbook.exbook.category.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.Repository

interface MongoCategoryRepository : Repository<CategoryDocument, String> {

    fun findById(id: String): CategoryDocument?

    fun findAll(): List<CategoryDocument>

    fun save(categoryDocument: CategoryDocument): CategoryDocument
}

@Document(collection = "categories")
data class CategoryDocument(
    @Id
    val id: String? = null,
    val name: String,
    val image: ImageDocument? = null,
    val parentId: String? = null
)

data class ImageDocument(val url: String)
