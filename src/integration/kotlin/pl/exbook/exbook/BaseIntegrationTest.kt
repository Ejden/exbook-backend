package pl.exbook.exbook

import org.junit.jupiter.api.BeforeEach
import kotlin.text.Regex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import pl.exbook.exbook.assertions.CategoriesDtoAssertions
import pl.exbook.exbook.category.adapter.mongodb.CategoryDocument
import pl.exbook.exbook.category.domain.CategoryRepository
import pl.exbook.exbook.security.adapter.rest.LoginCredentials
import pl.exbook.exbook.user.adapter.mongodb.UserDocument
import pl.exbook.exbook.user.domain.UserRepository

@SpringBootTest(classes = [AppRunner::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
@AutoConfigureMockMvc
internal class BaseIntegrationTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var mongoOps: MongoOperations

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun cleanup() {
        mongoOps.dropCollection(CategoryDocument::class.java)
        mongoOps.dropCollection(UserDocument::class.java)
    }

    fun assertThatCategories(): CategoriesDtoAssertions {
        return CategoriesDtoAssertions.assert(getAllCategories().body)
    }

//    fun thereIsUser(userBuilder: UserBuilder) {
//        userRepository.insert(userBuilder.build())
//    }

//    fun thereIsCategory(categoryBuilder: CategoryBuilder) {
//        categoryRepository.save(categoryBuilder.build())
//    }

    fun getDetailedUser(userId: String): ResponseEntity<Any>  {
        return testRestTemplate.getForEntity("/api/me", Any::class.java)
    }

    fun getSimplifiedUser(userId: String): ResponseEntity<Any> {
        return testRestTemplate.getForEntity("/api/users/$userId", Any::class.java)
    }

    fun getAllCategories(): ResponseEntity<Any>  {
        return testRestTemplate.getForEntity("/api/categories", Any::class.java)
    }

//    fun addNewCategory(category: NewCategory): ResponseEntity<Any> {
//        return testRestTemplate.postForEntity("/api/categories", category, Any::class.java)
//    }

//    fun addNewCategoryWithCredentials(category: NewCategory, credentials: LoginCredentials): ResponseEntity<Any> {
//        val httpEntity = HttpEntity(category, getHeadersWithAutorization(credentials))
//        return testRestTemplate.postForEntity("/api/categories", httpEntity, Any::class.java)
//    }

    private fun getTokenWithUserCredentials(credentials: LoginCredentials): String {
        val regex = Regex("\\[Authorization=(.*); Max-Age=(.*); Expires=(.*); Path=(.*)]")
        val response = testRestTemplate.postForEntity("/api/auth/login", credentials, Any::class.java)
                .headers["Set-Cookie"]
                .toString()
        return regex.matchEntire(response)!!.groups[1]!!.value
    }

    private fun getHeadersWithAutorization(credentials: LoginCredentials): HttpHeaders {
        val headers = HttpHeaders()
        headers.add("Cookie", "Authorization=${getTokenWithUserCredentials(credentials)}")
        return headers
    }
}
