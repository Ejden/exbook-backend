package pl.exbook.exbook.category.adapter.rest

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.shared.MediaType

@RestController
@RequestMapping("api/categories")
class CategoryEndpoint(private val categoryFacade: CategoryFacade) {

    @GetMapping(produces = [MediaType.V1])
    fun getAllCategories() : Collection<CategoryDto> {
        return categoryFacade.getAllCategories().map { it.toDto() }
    }

    @PostMapping(produces = [MediaType.V1])
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