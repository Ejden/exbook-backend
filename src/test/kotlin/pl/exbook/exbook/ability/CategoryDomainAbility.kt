package pl.exbook.exbook.ability

import pl.exbook.exbook.adapters.InMemoryCategoryRepository
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryCreator
import pl.exbook.exbook.category.domain.CreateCategoryCommand
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.TestData.sampleCategoryName

class CategoryDomainAbility {
    private val categoryRepository: InMemoryCategoryRepository = InMemoryCategoryRepository()
    private val categoryCreator: CategoryCreator = CategoryCreator(categoryRepository)
    val facade: CategoryFacade = CategoryFacade(categoryRepository, categoryCreator)

    fun thereIsCategory(name: String = sampleCategoryName, parentId: CategoryId? = null): Category {
        return facade.addCategory(
            CreateCategoryCommand(
                name = name,
                parentId = parentId
            )
        )
    }
}
