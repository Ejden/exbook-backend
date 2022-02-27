package pl.exbook.exbook.adapters

import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryRepository
import pl.exbook.exbook.shared.CategoryId

class InMemoryCategoryRepository : CategoryRepository {
    private val memory = mutableMapOf<CategoryId, Category>()

    override fun save(category: Category): Category {
        memory[category.id] = category
        return memory[category.id]!!
    }

    override fun getById(id: CategoryId): Category? = memory[id]

    override fun findByName(name: String): Category? = memory.values.firstOrNull { it.name == name }

    override fun findAll(): List<Category> = memory.values.toList()
}
