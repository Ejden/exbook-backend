package pl.exbook.exbook.category.domain

class Category(
    val id: CategoryId,
    val name: String,
    val image: Image? = null,
    val parentId: CategoryId? = null
)

data class Image(val url: String)

data class CategoryId(val raw: String)
