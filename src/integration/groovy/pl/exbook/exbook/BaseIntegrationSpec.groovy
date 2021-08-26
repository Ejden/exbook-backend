package pl.exbook.exbook

import kotlin.text.Regex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import pl.exbook.exbook.assertions.CategoriesDtoAssertions
import pl.exbook.exbook.builders.CategoryBuilder
import pl.exbook.exbook.builders.UserBuilder
import pl.exbook.exbook.category.adapter.mongodb.CategoryDocument
import pl.exbook.exbook.category.adapter.rest.NewCategory
import pl.exbook.exbook.category.domain.CategoryRepository
import pl.exbook.exbook.security.adapter.rest.LoginCredentials
import pl.exbook.exbook.user.adapter.mongodb.UserDocument
import pl.exbook.exbook.user.adapter.mongodb.MongoUserRepository
import pl.exbook.exbook.user.domain.UserRepository
import spock.lang.Specification

@SpringBootTest(classes = [AppRunner],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
@AutoConfigureMockMvc
class BaseIntegrationSpec extends Specification {

    @Autowired
    protected TestRestTemplate testRestTemplate

    @Autowired
    private UserRepository userRepository

    @Autowired
    private CategoryRepository categoryRepository

    @Autowired
    private MongoOperations mongoOps

    @Autowired
    protected MockMvc mockMvc

    void setup() {

    }

    void cleanup() {
        mongoOps.dropCollection(CategoryDocument)
        mongoOps.dropCollection(UserDocument)
    }

    protected CategoriesDtoAssertions assertThatCategories() {
        return CategoriesDtoAssertions.assertThat(getAllCategories().body)
    }

    protected thereIsUser(UserBuilder userBuilder) {
        userRepository.insert(userBuilder.build())
    }

    protected thereIsCategory(CategoryBuilder categoryBuilder) {
        categoryRepository.save(categoryBuilder.build())
    }

    protected ResponseEntity<Object> getDetailedUser(String userId) {
        return testRestTemplate.getForEntity("/api/me", Object.class)
    }

    protected ResponseEntity<Object> getSimplifiedUser(String userId) {
        return testRestTemplate.getForEntity("/api/users/$userId", Object.class)
    }

    protected ResponseEntity<Object> getAllCategories() {
        return testRestTemplate.getForEntity("/api/categories", Object.class)
    }

    protected ResponseEntity<Object> addNewCategory(NewCategory category) {
        return testRestTemplate.postForEntity("/api/categories", category, Object.class)
    }

    protected ResponseEntity<Object> addNewCategoryWithCredentials(NewCategory category, LoginCredentials credentials) {
        HttpEntity httpEntity = new HttpEntity(category, getHeadersWithAutorization(credentials))
        return testRestTemplate.postForEntity("/api/categories", httpEntity, Object.class)
    }

    private String getTokenWithUserCredentials(LoginCredentials credentials) {
        Regex regex = new Regex("\\[Authorization=(.*); Max-Age=(.*); Expires=(.*); Path=(.*)\\]")
        String response = testRestTemplate.postForEntity("/api/auth/login", credentials, Object.class)
                .headers["Set-Cookie"]
                .toString()
        return regex.matchEntire(response).groups[1].value
    }

    private HttpHeaders getHeadersWithAutorization(LoginCredentials credentials) {
        HttpHeaders headers = new HttpHeaders()
        headers.add("Cookie", "Authorization=${getTokenWithUserCredentials(credentials)}")
        return headers
    }
}
