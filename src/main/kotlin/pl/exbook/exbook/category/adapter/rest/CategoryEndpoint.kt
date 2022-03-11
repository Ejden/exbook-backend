package pl.exbook.exbook.category.adapter.rest

import javax.validation.Valid
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.adapter.rest.dto.CategoriesDto
import pl.exbook.exbook.category.adapter.rest.dto.CategoriesNodesDto
import pl.exbook.exbook.category.adapter.rest.dto.CategoryDto
import pl.exbook.exbook.category.adapter.rest.dto.CreateCategoryRequest
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryNode
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.ContentType

@RestController
@RequestMapping("api/categories")
class CategoryEndpoint(private val categoryFacade: CategoryFacade) {

    @GetMapping(produces = [ContentType.V1])
    fun getAllCategories(@RequestParam(defaultValue = "flat") structure: String): Any {
        return when (structure) {
            "tree" -> categoryFacade.getCategoriesTree().toDto()
            else -> categoryFacade.getAllCategories().toDto()
        }
    }

    @PostMapping(produces = [ContentType.V1], consumes = [ContentType.V1])
    @Secured("ROLE_ADMIN")
    fun addCategory(@RequestBody @Valid requestBody: CreateCategoryRequest): CategoryDto {
        return categoryFacade.addCategory(requestBody.toCommand()).toDto()
    }

    @GetMapping("{categoryId}", produces = [ContentType.V1])
    fun getCategory(@PathVariable categoryId: CategoryId): CategoryDto {
        return categoryFacade.getCategory(categoryId).toDto()
    }
}

private fun List<Category>.toDto() = CategoriesDto.fromDomain(this)
private fun List<CategoryNode>.toDto() = CategoriesNodesDto.fromDomain(this)
private fun Category.toDto() = CategoryDto.fromDomain(this)
