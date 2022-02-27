package pl.exbook.exbook.category.adapter.rest.dto

import pl.exbook.exbook.category.domain.CategoryNode

data class CategoryNodeDto(
    val id: String,
    val name: String,
    val icon: ImageDto,
    val parentId: String?,
    val children: List<CategoryNodeDto>
) {
    data class ImageDto(val url: String?)

    companion object {
        fun fromDomain(category: CategoryNode): CategoryNodeDto = CategoryNodeDto(
            id = category.id.raw,
            name = category.name,
            icon = ImageDto(category.image?.url),
            parentId = category.parentId?.raw,
            children = category.children.map { fromDomain(it) }
        )
    }
}
