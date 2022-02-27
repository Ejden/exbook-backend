package pl.exbook.exbook.category.adapter.rest

import javax.validation.Valid
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.adapter.rest.dto.CategoryDto
import pl.exbook.exbook.category.adapter.rest.dto.CategoryNodeDto
import pl.exbook.exbook.category.adapter.rest.dto.CreateCategoryRequest
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

    @PostMapping(produces = [ContentType.V1], consumes = [ContentType.V1])
    @Secured("ROLE_ADMIN")
    fun addCategory(@RequestBody @Valid requestBody: CreateCategoryRequest): Category? {
        return categoryFacade.addCategory(requestBody.toCommand())
    }
}

private fun Category.toDto() = CategoryDto.fromDomain(this)
private fun CategoryNode.toDto() = CategoryNodeDto.fromDomain(this)
