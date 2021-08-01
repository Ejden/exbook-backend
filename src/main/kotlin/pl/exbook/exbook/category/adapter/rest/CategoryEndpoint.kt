package pl.exbook.exbook.category.adapter.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.Category

const val CONTENT_TYPE = "application/vnd.exbook.v1+json"

@RestController
@RequestMapping("api/categories")
class CategoryEndpoint(private val categoryFacade: CategoryFacade) {

    @GetMapping(produces = [CONTENT_TYPE])
    fun getAllCategories() : Collection<Category> {
        return categoryFacade.getAllCategories()
    }

    @PostMapping(produces = [CONTENT_TYPE])
    fun addCategory(@RequestBody requestBody: NewCategoryRequest): Category? {
        return categoryFacade.addCategory(requestBody)
    }
}

data class NewCategoryRequest(
    val name: String,
    val parentId: String?
)
