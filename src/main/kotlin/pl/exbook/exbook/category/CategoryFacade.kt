package pl.exbook.exbook.category

import pl.exbook.exbook.category.adapter.rest.NewCategory
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryRepository

class CategoryFacade(private val categoryRepository: CategoryRepository) {

    fun getAllCategories(): Collection<Category> {
        return categoryRepository.findAll()
    }

    fun addCategory(newCategory: NewCategory): Category {
        return categoryRepository.save(newCategory)
    }
}

