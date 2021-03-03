package pl.exbook.exbook.category

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface CategoryRepository: MongoRepository<CategoryDatabaseModel, String> {
    @Query(value = "{_id: ?0}", fields = "{svgImg: 1}")
    fun getThumbnail(id: String): Thumbnail?
}

@Document(collection = "categories")
data class CategoryDatabaseModel(
    val id: String,
    val name: String,
    val svgImg: String?,
    var parentId: String?
) {
    fun toCategory(): Category {
        return Category(id, name, svgImg)
    }
}

data class Thumbnail(
    val id: String?,
    val svgImg: String?
)