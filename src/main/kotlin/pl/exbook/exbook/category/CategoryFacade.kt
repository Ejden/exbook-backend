package pl.exbook.exbook.category

import pl.exbook.exbook.category.adapter.rest.NewCategory
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryNode
import pl.exbook.exbook.category.domain.CategoryRepository

class CategoryFacade(private val categoryRepository: CategoryRepository) {

    fun getAllCategories(): Collection<Category> {
        return categoryRepository.findAll()
    }

    fun addCategory(newCategory: NewCategory): Category {
        return categoryRepository.save(newCategory)
    }

    fun getCategoriesTree(): List<CategoryNode> = makeTree(categoryRepository
        .findAll()
        .map { CategoryNode(it.id, it.name, it.image, it.parentId) }
    )

    private fun makeTree(categories: List<CategoryNode>): List<CategoryNode> {
        categories
            .forEach {
                if (it.parentId != null)
                    categories
                        .find { searchingCategory -> it.parentId == searchingCategory.id }
                        ?.children?.add(it)
            }
        return categories.filter { it.parentId == null }
    }
}
