package pl.exbook.exbook.category

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

interface CategoryRepository: MongoRepository<CategoryDatabaseModel, String> {
}

@Document(collection = "categories")
data class CategoryDatabaseModel(
    val id: String,
    val name: String,
    var parentId: String?
) {
    fun toCategory(): Category {
        return Category(id, name)
    }
}