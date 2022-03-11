package pl.exbook.exbook.category

import org.springframework.stereotype.Service
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryCreator
import pl.exbook.exbook.category.domain.CategoryNode
import pl.exbook.exbook.category.domain.CategoryNotFoundException
import pl.exbook.exbook.category.domain.CategoryRepository
import pl.exbook.exbook.category.domain.CreateCategoryCommand
import pl.exbook.exbook.shared.CategoryId

@Service
class CategoryFacade(
    private val categoryRepository: CategoryRepository,
    private val categoryCreator: CategoryCreator
) {
    fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    fun addCategory(command: CreateCategoryCommand): Category {
        return categoryCreator.createCategory(command)
    }

    fun getCategory(categoryId: CategoryId): Category {
        return categoryRepository.getById(categoryId) ?: throw CategoryNotFoundException(categoryId)
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
