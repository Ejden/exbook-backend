package pl.exbook.exbook.category.adapter.mongodb

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.Image

interface CategoryRepository: MongoRepository<CategoryDatabaseModel, String>

@Document(collection = "categories")
data class CategoryDatabaseModel(
    val id: String? = null,
    val name: String,
    val image: ImageDatabaseModel? = null,
    var parentId: String?
) {
    fun toDomain() = Category(id!!, name, image?.toDomain())
}

data class ImageDatabaseModel(val url: String) {
    fun toDomain() = Image(this.url)
}
