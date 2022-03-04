package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.category.adapter.rest.dto.CategoryDto
import pl.exbook.exbook.category.adapter.rest.dto.CreateCategoryRequest
import pl.exbook.exbook.shared.TestData.sampleCategoryName
import pl.exbook.exbook.utils.createHttpEntity

class CategoryDomainAbility(private val restTemplate: TestRestTemplate) {
    fun getCategories(): ResponseEntity<String> {
        return restTemplate.getForEntity(API_URL, String::class.java)
    }

    fun getCategoriesTree(): ResponseEntity<String> {
        return restTemplate.getForEntity("$API_URL?structure=tree", String::class.java)
    }

    fun getCategory(categoryId: String): ResponseEntity<String> {
        return restTemplate.getForEntity("$API_URL/${categoryId}", String::class.java)
    }

    fun addCategory(requestBody: CreateCategoryRequest, token: String? = null): ResponseEntity<String> {
        val httpEntity = createHttpEntity(
            body = requestBody,
            withAcceptHeader = true,
            withContentTypeHeader = true,
            token = token
        )
        return restTemplate.postForEntity(API_URL, httpEntity, String::class.java)
    }

    fun thereIsCategory(name: String = sampleCategoryName, parentId: String? = null, token: String? = null): ResponseEntity<CategoryDto> {
        val requestBody = CreateCategoryRequest(name, parentId)
        val httpEntity = createHttpEntity(
            body = requestBody,
            withContentTypeHeader = true,
            token = token
        )
        return restTemplate.postForEntity(API_URL, httpEntity, CategoryDto::class.java)
    }

    companion object {
        private const val API_URL = "/api/categories"
    }
}
