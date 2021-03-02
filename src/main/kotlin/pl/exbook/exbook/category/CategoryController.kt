package pl.exbook.exbook.category

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}

data class NewCategoryRequest(
    val name: String,
    val parentId: String?
)