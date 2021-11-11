package pl.exbook.exbook.builders

import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.Image
import pl.exbook.exbook.shared.CategoryId

class CategoryBuilder private constructor() {

    private var id: String = "category-id"
    private var name: String = "category-name"
    private var image: Image = Image("https://files.exbook.com/images/123")
    private var parentId: String? = null

    companion object {
        fun aCategoryBuilder(): CategoryBuilder {
            return CategoryBuilder()
        }
    }

    fun withId(id: String): CategoryBuilder {
        this.id = id
        return this
    }

    fun withName(id: String): CategoryBuilder {
        this.name = name
        return this
    }

    fun withImage(image: Image): CategoryBuilder {
        this.image = image
        return this
    }

    fun withParentId(parentId: String): CategoryBuilder {
        this.parentId = parentId
        return this
    }

    fun build(): Category {
        return Category(
                CategoryId(id),
                name,
                image,
                parentId?.let { CategoryId(it) }
        )
    }
}
