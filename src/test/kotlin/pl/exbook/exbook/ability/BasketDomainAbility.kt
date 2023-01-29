package pl.exbook.exbook.ability

import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import pl.exbook.exbook.adapters.InMemoryBasketRepository
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.basket.domain.BasketDetailsDecorator
import pl.exbook.exbook.basket.domain.BasketFactory
import pl.exbook.exbook.basket.domain.BasketValidator
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferNotFoundException
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.TestData
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserNotFoundException

class BasketDomainAbility {
    private val basketRepository: InMemoryBasketRepository = InMemoryBasketRepository()
    private val userFacade: UserFacade = mockk()
    private val offerFacade: OfferFacade = mockk()
    private val validator: BasketValidator = BasketValidator()
    private val basketFactory: BasketFactory = BasketFactory()
    private val basketDetailsDecorator: BasketDetailsDecorator = BasketDetailsDecorator()
    val facade = BasketFacade(
        basketRepository = basketRepository,
        userFacade = userFacade,
        offerFacade = offerFacade,
        validator = validator,
        basketFactory = basketFactory,
        basketDetailsDecorator = basketDetailsDecorator
    )

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
        bookAuthor: String = "Jan",
        bookTitle: String = "Jan na drzewie",
        thumbnailUrl: String? = TestData.sampleImageUrl,
        allImagesUrls: List<String> = emptyList(),
        description: String = "Offer description",
        type: Offer.Type = Offer.Type.EXCHANGE_AND_BUY,
        sellerId: UserId = TestData.sampleSellerId,
        price: Money? = TestData.tenPln,
        location: String = "Warsaw",
        category: Offer.Category = Offer.Category(
            id = TestData.sampleCategoryId
        ),
        shippingMethods: List<Offer.ShippingMethod> = listOf(
            Offer.ShippingMethod(
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
            book = Offer.Book(
                author = bookAuthor,
                title = bookTitle,
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            images = Offer.Images(
                thumbnail = thumbnailUrl?.let { Offer.Image(it) },
                allImages = allImagesUrls.map { Offer.Image(it) },
            ),
            description = description,
            type = type,
            seller = Offer.Seller(
                id = sellerId
            ),
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

    fun thereIsNoOfferFor(offerId: OfferId) {
        every { offerFacade.getOffer(offerId) } throws OfferNotFoundException(offerId)
    }

    fun thereIsNoUserFor(userId: UserId, username: String) {
        every { userFacade.getUserById(userId) } throws UserNotFoundException("")
        every { userFacade.getUserByUsername(username) } throws UserNotFoundException("")
    }
}

data class BasketItem(
    val buyerId: UserId,
    val buyerUsername: String,
    val sellerId: UserId,
    val sellerUsername: String,
    val offerId: OfferId,
    val quantity: Int
)
