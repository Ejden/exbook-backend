package pl.exbook.exbook.category.domain

import pl.exbook.exbook.shared.CategoryId

class Category(
    val id: CategoryId,
    val name: String,
    val image: Image? = null,
    val parentId: CategoryId? = null
)

data class Image(val url: String)

data class CategoryNode(
    val id: CategoryId,
    val name: String,
    val image: Image? = null,
    val parentId: CategoryId?,
    val children: MutableList<CategoryNode> = mutableListOf()
)
