package pl.exbook.exbook.category.adapter.rest.dto

import pl.exbook.exbook.category.domain.CreateCategoryCommand
import pl.exbook.exbook.shared.CategoryId

data class CreateCategoryRequest(
    val name: String,
    val parentId: String?
) {
    fun toCommand() = CreateCategoryCommand(
        name = this.name,
        parentId = this.parentId?.let { CategoryId(it) }
    )
}
