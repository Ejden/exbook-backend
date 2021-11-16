package pl.exbook.exbook.category.adapter.rest

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryNode
import pl.exbook.exbook.shared.ContentType

@RestController
@RequestMapping("api/categories")
class CategoryEndpoint(private val categoryFacade: CategoryFacade) {

    @GetMapping(produces = [ContentType.V1])
    fun getAllCategories(@RequestParam(defaultValue = "flat") structure: String): Collection<Any> {
        return when (structure) {
            "tree" -> categoryFacade.getCategoriesTree().map { it.toDto() }
            else -> categoryFacade.getAllCategories().map { it.toDto() }
        }
    }

    @PostMapping(produces = [ContentType.V1])
    @Secured("ROLE_ADMIN")
    fun addCategory(@RequestBody requestBody: NewCategory): Category? {
        return categoryFacade.addCategory(requestBody)
    }
}

data class NewCategory(
    val name: String,
    val parentId: String?
)

data class CategoryDto(
    val id: String,
    val name: String,
    val icon: ImageDto,
    val parentId: String?
)

data class ImageDto(val url: String?)

private fun Category.toDto() = CategoryDto(
    id = this.id.raw,
    name = this.name,
    icon = ImageDto(this.image?.url),
    parentId = this.parentId?.raw
)

data class CategoryNodeDto(
    val id: String,
    val name: String,
    val icon: ImageDto,
    val parentId: String?,
    val children: List<CategoryNodeDto>
)

private fun CategoryNode.toDto(): CategoryNodeDto = CategoryNodeDto(
    id = this.id.raw,
    name = this.name,
    icon = ImageDto(this.image?.url),
    parentId = this.parentId?.raw,
    children = this.children.map { it.toDto() }
)
