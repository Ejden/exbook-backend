package pl.exbook.exbook.category.adapter.mongodb

import org.springframework.stereotype.Component
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.category.domain.CategoryRepository
import pl.exbook.exbook.category.domain.Image

@Component
class DatabaseCategoryRepository(private val mongoCategoryRepository: MongoCategoryRepository) : CategoryRepository {

    override fun save(category: Category): Category {
        return mongoCategoryRepository.save(category.toDocument()).toDomain()
    }

    override fun getById(id: CategoryId): Category? {
        return mongoCategoryRepository.findById(id.raw)?.toDomain()
    }

    override fun findByName(name: String): Category? {
        return mongoCategoryRepository.findByName(name)?.toDomain()
    }

    override fun findAll(): List<Category> {
        return mongoCategoryRepository.findAll().map { it.toDomain() }
    }
}

private fun Category.toDocument() = CategoryDocument(
    id = this.id.raw,
    name = this.name,
    image = this.image?.toDocument(),
    parentId = this.parentId?.raw
)

private fun Image.toDocument() = ImageDocument(this.url)

private fun CategoryDocument.toDomain() = Category(
    id = CategoryId(this.id),
    name = this.name,
    image = this.image?.toDomain(),
    parentId = this.parentId?.let { CategoryId(it) }
)

private fun ImageDocument.toDomain() = Image(this.url)
