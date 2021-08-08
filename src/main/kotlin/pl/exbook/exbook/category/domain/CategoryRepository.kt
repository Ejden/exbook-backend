package pl.exbook.exbook.category.domain

import pl.exbook.exbook.category.adapter.rest.NewCategory

interface CategoryRepository {

    fun save(category: Category): Category

    fun save(newCategory: NewCategory): Category

    fun getById(id: CategoryId): Category?

    fun findAll(): Collection<Category>
}
