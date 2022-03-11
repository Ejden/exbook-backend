package pl.exbook.exbook.category.adapter.rest.dto

import pl.exbook.exbook.category.domain.CategoryNode

data class CategoriesNodesDto(
    val categories: List<CategoryNodeDto>
) {
    companion object {
        fun fromDomain(categories: List<CategoryNode>): CategoriesNodesDto = CategoriesNodesDto(
            categories = categories.map { CategoryNodeDto.fromDomain(it) }
        )
    }
}

data class CategoryNodeDto(
    val id: String,
    val name: String,
    val icon: ImageDto?,
    val parentId: String?,
    val children: List<CategoryNodeDto>
) {
    data class ImageDto(val url: String?)

    companion object {
        fun fromDomain(category: CategoryNode): CategoryNodeDto = CategoryNodeDto(
            id = category.id.raw,
            name = category.name,
            icon = category.image?.let { ImageDto(it.url) },
            parentId = category.parentId?.raw,
            children = category.children.map { fromDomain(it) }
        )
    }
}
