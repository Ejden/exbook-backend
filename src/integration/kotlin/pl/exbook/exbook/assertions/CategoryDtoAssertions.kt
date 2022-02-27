package pl.exbook.exbook.assertions

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import pl.exbook.exbook.category.adapter.rest.dto.CategoryDto
import pl.exbook.exbook.category.adapter.rest.ImageDto

@Suppress("UNCHECKED_CAST")
class CategoryDtoAssertions private constructor(
    private val category: Map<String, Any>
) {

    companion object {
        fun assert(category: Any?): CategoryDtoAssertions {
            assertThat(category).isNotNull()
            return CategoryDtoAssertions(category as Map<String, Any>)
        }
    }

    fun hasId(id: String): CategoryDtoAssertions {
        assertThat(category["id"]).isEqualTo(id)
        return this
    }

    fun hasName(name: String): CategoryDtoAssertions {
        assertThat(category["name"]).isEqualTo(name)
        return this
    }

    fun hasIconUrl(url: String): CategoryDtoAssertions {
        assertThat((category["icon"] as Map<*, *>)["url"]).isEqualTo(url)
        return this
    }

    fun hasParentId(parentId: String): CategoryDtoAssertions {
        assertThat(category["parentId"]).isEqualTo(parentId)
        return this
    }

    fun hasNoParentId(): CategoryDtoAssertions {
        assertThat(category["parentId"]).isNull()
        return this
    }
}

@Suppress("UNCHECKED_CAST")
class CategoriesDtoAssertions private constructor(
    private val categories: List<Any>
){

    companion object {
        fun assert(categories: Any?): CategoriesDtoAssertions {
            assertThat(categories).isNotNull()
            return CategoriesDtoAssertions(categories as List<Any>)
        }
    }

    fun hasCategories(vararg categories: CategoryDto): CategoriesDtoAssertions {
        assertThat(this.categories.map { it as Map<String, String> }.map {
            CategoryDto(
                it["id"]!!,
                it["name"]!!,
                ImageDto((it["icon"] as Map<String, Any>)["url"] as String),
                it["parentId"]
            )
        }).containsExactly(*categories)
        return this
    }

    fun hasNoCategories(): CategoriesDtoAssertions {
        assertThat(this.categories).isEmpty()
        return this
    }
}
