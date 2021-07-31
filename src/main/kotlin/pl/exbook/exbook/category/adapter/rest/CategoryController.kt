package pl.exbook.exbook.category.adapter.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.Category

const val CONTENT_TYPE = "application/vnd.exbook.v1+json"

@RestController
@RequestMapping("api/categories")
class CategoryController(val categoryFacade: CategoryFacade) {

    @GetMapping(consumes = [CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE])
    fun getAllCategories() : Collection<Category> {
        return categoryFacade.getAllCategories()
    }

    @PostMapping(consumes = [CONTENT_TYPE])
    fun addCategory(@RequestBody requestBody: NewCategoryRequest): Category? {
        return categoryFacade.addCategory(requestBody)
    }
}

data class NewCategoryRequest(
    val name: String,
    val parentId: String?
)
