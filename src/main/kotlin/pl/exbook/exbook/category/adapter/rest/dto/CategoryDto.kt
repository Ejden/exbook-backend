package pl.exbook.exbook.category.adapter.rest.dto

import pl.exbook.exbook.category.domain.Category

data class CategoriesDto(
    val categories: List<CategoryDto>
) {
    companion object {
        fun fromDomain(categories: List<Category>): CategoriesDto = CategoriesDto(
            categories = categories.map { CategoryDto.fromDomain(it) }
        )
    }
}

data class CategoryDto(
    val id: String,
    val name: String,
    val icon: ImageDto?,
    val parentId: String?
) {
    data class ImageDto(val url: String?)

    companion object {
        fun fromDomain(category: Category) = CategoryDto(
            id = category.id.raw,
            name = category.name,
            icon = category.image?.let { ImageDto(it.url) },
            parentId = category.parentId?.raw
        )
    }
}
