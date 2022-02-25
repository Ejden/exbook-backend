package pl.exbook.exbook.features.basket

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import org.junit.jupiter.api.BeforeEach
import pl.exbook.exbook.adapters.InMemoryBasketRepository
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.basket.domain.BasketDetailsDecorator
import pl.exbook.exbook.basket.domain.BasketFactory
import pl.exbook.exbook.basket.domain.BasketValidator
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.TestData
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User

abstract class BasketFacadeTestSpecification {
    private val basketRepository: InMemoryBasketRepository = InMemoryBasketRepository()
    private val userFacade: UserFacade = mockk()
    private val offerFacade: OfferFacade = mockk()
    private val validator: BasketValidator = BasketValidator()
    private val basketFactory: BasketFactory = BasketFactory()
    private val basketDetailsDecorator: BasketDetailsDecorator = BasketDetailsDecorator(offerFacade, userFacade)
    val basketFacade = BasketFacade(
        basketRepository = basketRepository,
        userFacade = userFacade,
        offerFacade = offerFacade,
        validator = validator,
        basketFactory = basketFactory,
        basketDetailsDecorator = basketDetailsDecorator
    )

    @BeforeEach
    fun cleanup() {
        clearMocks(offerFacade, userFacade)
    }

    fun thereIsUser(
        userId: UserId = TestData.sampleUserId,
        firstName: String = "Jan",
        lastName: String = "Kowalski",
        username: String = "j.kowalski",
        password: String = "password",
        email: String = "j.kowalski@gmail.com",
        phoneNumber: String = "555555555",
        enabled: Boolean = true,
        active: Boolean = true,
        locked: Boolean = false,
        credentialExpired: Boolean = false,
        creationDate: Instant = Instant.EPOCH,
        grade: Double = 5.0
    ) {
        val mockUser = User(
            id = userId,
            firstName = firstName,
            lastName = lastName,
            username = username,
            password = password,
            email = email,
            phoneNumber = phoneNumber,
            enabled = enabled,
            active = active,
            locked = locked,
            credentialExpired = credentialExpired,
            authorities = mutableSetOf(),
            creationDate = creationDate,
            grade = grade
        )
        every { userFacade.getUserById(userId) } returns mockUser
        every { userFacade.getUserByUsername(username) } returns mockUser
    }

    fun thereIsOffer(
        offerId: OfferId = TestData.sampleOfferId,
        versionId: OfferVersionId = TestData.sampleOfferVersionId,
        versionCreationDate: Instant = Instant.EPOCH,
        versionExpireDate: Instant? = null,
        book: Offer.Book = Offer.Book(
            author = "Jan",
            title = "Jan na drzewie",
            isbn = 123123123L,
            condition = Offer.Condition.NEW
        ),
        images: Offer.Images = Offer.Images(
            thumbnail = Offer.Image(TestData.sampleImageUrl),
            allImages = listOf(Offer.Image(TestData.sampleImageUrl))
        ),
        description: String = "Offer description",
        type: Offer.Type = Offer.Type.EXCHANGE_AND_BUY,
        seller: Offer.Seller = Offer.Seller(
            id = TestData.sampleSellerId
        ),
        price: Money? = TestData.tenPln,
        location: String = "Warsaw",
        category: Offer.Category = Offer.Category(
            id = TestData.sampleCategoryId
        ),
        shippingMethods: Collection<Offer.ShippingMethod> = listOf(Offer.ShippingMethod(
            id = TestData.sampleShippingMethodId,
            price = TestData.tenPln
        )),
        stockId: StockId = TestData.sampleStockId
    ) {
        every { offerFacade.getOffer(offerId) } returns Offer(
            id = offerId,
            versionId = versionId,
            versionCreationDate = versionCreationDate,
            versionExpireDate = versionExpireDate,
            book = book,
            images = images,
            description = description,
            type = type,
            seller = seller,
            price = price,
            location = location,
            category = category,
            shippingMethods = shippingMethods,
            stockId = stockId
        )
    }

    fun thereIsNoBasketFor(userId: UserId) {
        basketRepository.removeBasket(userId)
    }
}
