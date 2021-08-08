package pl.exbook.exbook.category.adapter.mongodb

import pl.exbook.exbook.category.adapter.rest.NewCategory
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryId
import pl.exbook.exbook.category.domain.CategoryRepository
import pl.exbook.exbook.category.domain.Image

class DatabaseCategoryRepository(private val mongoCategoryRepository: MongoCategoryRepository) : CategoryRepository {

    override fun save(category: Category): Category {
        return mongoCategoryRepository.save(category.toDocument()).toDomain()
    }

    override fun save(newCategory: NewCategory): Category {
        return mongoCategoryRepository.save(newCategory.toDocument()).toDomain()
    }

    override fun getById(id: CategoryId): Category? {
        return mongoCategoryRepository.findById(id.raw)?.toDomain()
    }

    override fun findAll(): Collection<Category> {
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
    id = CategoryId(this.id!!),
    name = this.name,
    image = this.image?.toDomain(),
    parentId = this.parentId?.let { CategoryId(it) }
)

private fun ImageDocument.toDomain() = Image(this.url)

private fun NewCategory.toDocument() = CategoryDocument(
    name = this.name,
    parentId = this.parentId
)
