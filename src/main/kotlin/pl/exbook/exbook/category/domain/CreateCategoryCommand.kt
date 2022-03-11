package pl.exbook.exbook.category.domain

import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.IllegalParameterException

data class CreateCategoryCommand(
    val name: String,
    val parentId: CategoryId?
) {
    init {
        if (name.isEmpty()) {
            throw IllegalParameterException("category name should not be empty")
        }
    }
}
