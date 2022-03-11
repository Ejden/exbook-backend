package pl.exbook.exbook.category.domain

import pl.exbook.exbook.shared.CategoryId

interface CategoryRepository {
    fun save(category: Category): Category

    fun getById(id: CategoryId): Category?

    fun findByName(name: String): Category?

    fun findAll(): List<Category>
}
