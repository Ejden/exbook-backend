package pl.exbook.exbook


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import pl.exbook.exbook.AppRunner
import pl.exbook.exbook.TestMongoConfig
import pl.exbook.exbook.builders.CategoryBuilder
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
import spock.lang.Specification

@SpringBootTest(classes = [AppRunner],
    properties = "application.environment=integration",
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataMongo
@ActiveProfiles("integration")
class BaseIntegrationSpec extends Specification {

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
}
