package pl.exbook.exbook

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import pl.exbook.exbook.builders.UserBuilder
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.CategoryRepository
import pl.exbook.exbook.image.ImageFacade
import pl.exbook.exbook.listing.ListingFacade
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.adapter.mongodb.UserDocument
import pl.exbook.exbook.user.adapter.mongodb.UserRepository
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(classes = [AppRunner],
    properties = "application.environment=integration",
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
class BaseIntegrationSpec extends Specification {

    @Shared
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        mongoDBContainer.start()
        registry.add("spring.datasource.uri", { mongoDBContainer.replicaSetUrl })
        registry.add("spring.datasource.host", { "localhost" })
        registry.add("spring.datasource.port", { mongoDBContainer.livenessCheckPortNumbers[0] })
        registry.add("spring.datasource.database", { "test" })
    }

    @Autowired
    private MongoTemplate mongoTemplate

    @Autowired
    protected TestRestTemplate testRestTemplate

    @Autowired
    private UserRepository userRepository

    @Autowired
    private CategoryRepository categoryRepository

    @Autowired
    protected UserFacade userFacade

    @Autowired
    protected CategoryFacade categoryFacade

    @Autowired
    protected ImageFacade ImageFacade

    @Autowired
    protected ListingFacade listingFacade

    @Autowired
    protected OfferFacade offerFacade

    @Autowired
    protected ShippingFacade shippingFacade

    void setup() {

    }

    void cleanup() {
        mongoTemplate.dropCollection(UserDocument)
    }

    protected thereIsUser(UserBuilder userBuilder) {
        userRepository.insert(userBuilder.build().toDocument())
    }

    protected thereIsCategory(CategoryBuilder categoryBuilder) {
        categoryRepository.save()
    }

    protected ResponseEntity<Object> getDetailedUser(String userId) {
        return testRestTemplate.getForEntity("/api/me", Object.class)
    }

    protected ResponseEntity<Object> getSimplifiedUser(String userId) {
        return testRestTemplate.getForEntity("/api/users/$userId", Object.class)
    }
}
