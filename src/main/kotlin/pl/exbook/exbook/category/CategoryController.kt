package pl.exbook.exbook.category

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/categories")
class CategoryController(val categoryService: CategoryService) {

    @GetMapping
    fun getAllCategories() : Collection<Category> {
        return categoryService.getAllCategories()
    }

    @PostMapping
    fun addCategory(requestBody: NewCategoryRequest): Category? {
        return categoryService.addCategory(requestBody)
    }

    @GetMapping("{id}/thumbnail")
    fun getThumbnail(@PathVariable id: String): HttpEntity<String?>? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.valueOf("image/svg+xml")
        return HttpEntity<String?>(categoryService.getCategoryThumbnail(id), headers)
    }
}

data class NewCategoryRequest(
    val name: String,
    val parentId: String?
)