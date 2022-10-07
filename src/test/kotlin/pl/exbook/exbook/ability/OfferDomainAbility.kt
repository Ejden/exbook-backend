package pl.exbook.exbook.ability

import io.mockk.every
import io.mockk.mockk
import pl.exbook.exbook.adapters.InMemoryOfferRepository
import pl.exbook.exbook.adapters.InMemoryOfferVersioningRepository
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.domain.Category
import pl.exbook.exbook.category.domain.CategoryNotFoundException
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.CreateOfferCommand
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferCreator
import pl.exbook.exbook.offer.domain.OfferValidator
import pl.exbook.exbook.offer.domain.OfferVersioningService
import pl.exbook.exbook.pln
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.TestData
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleCategoryName
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleStockId
import pl.exbook.exbook.shared.TestData.sampleUserId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.domain.Cost
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod
import pl.exbook.exbook.stock.StockFacade
import pl.exbook.exbook.stock.domain.Stock
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.domain.User
import pl.exbook.exbook.user.domain.UserNotFoundException
import java.time.Instant
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType

class OfferDomainAbility {
    private val offerRepository: InMemoryOfferRepository = InMemoryOfferRepository()
    private val offerVersioningRepository: InMemoryOfferVersioningRepository = InMemoryOfferVersioningRepository()
    private val offerVersioningService: OfferVersioningService = OfferVersioningService(offerVersioningRepository)
    private val userFacade: UserFacade = mockk()
    private val stockFacade: StockFacade = mockk()
    private val shippingMethodFacade: ShippingMethodFacade = mockk()
    private val categoryFacade: CategoryFacade = mockk()
    private val offerValidator: OfferValidator = OfferValidator(shippingMethodFacade, categoryFacade)
    private val offerCreator: OfferCreator = OfferCreator(
        offerRepository = offerRepository,
        offerVersioningService = offerVersioningService,
        userFacade = userFacade,
        stockFacade = stockFacade,
        offerValidator = offerValidator
    )
    val facade: OfferFacade = OfferFacade(
        offerRepository = offerRepository,
        offerVersioningRepository = offerVersioningRepository,
        offerCreator = offerCreator,
        userFacade = userFacade
    )

    fun thereIsUser(
        userId: UserId = sampleUserId,
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

    fun thereIsNoUser(userId: UserId = sampleUserId, username: String = "jan.kowalski") {
        every { userFacade.getUserById(userId) } throws UserNotFoundException(userId)
        every { userFacade.getUserByUsername(username) } throws UserNotFoundException(username)
    }

    fun stockFacadeWillCreateStockForOffer(
        stockId: StockId = sampleStockId,
        initialStock: Long = 100L
    ) {
        every { stockFacade.createStock(startQuantity = initialStock) } returns Stock(stockId, initialStock, 0)
        every { stockFacade.getStock(sampleStockId) } returns Stock(stockId, initialStock, 0)
    }

    fun thereIsShippingMethod(
        shippingMethodId: ShippingMethodId = sampleShippingMethodId,
        methodName: String = "Shipping method",
        type: ShippingMethodType = ShippingMethodType.PICKUP_DELIVERY,
        cost: Money = "8.99".pln(),
        costCanBeOverridden: Boolean = true
    ) {
        val mockShippingMethod = ShippingMethod(
            id = shippingMethodId,
            methodName = methodName,
            type = type,
            defaultCost = Cost(
                cost = cost,
                canBeOverridden = costCanBeOverridden
            )
        )

        every { shippingMethodFacade.getShippingMethod(shippingMethodId) } returns mockShippingMethod
        every { shippingMethodFacade.getShippingMethodById(shippingMethodId) } returns mockShippingMethod
        every { shippingMethodFacade.getShippingMethods() } returns listOf(mockShippingMethod)
    }

    fun thereIsNoShippingMethod(shippingMethodId: ShippingMethodId = sampleShippingMethodId) {
        every { shippingMethodFacade.getShippingMethod(shippingMethodId) } returns null
    }

    fun thereIsCategory(
        categoryId: CategoryId,
        name: String = sampleCategoryName,
        parentId: CategoryId? = null
    ) {
        every { categoryFacade.getCategory(categoryId) } returns Category(categoryId, name, null, parentId)
    }

    fun thereIsNoCategory(categoryId: CategoryId) {
        every { categoryFacade.getCategory(categoryId) } throws CategoryNotFoundException(categoryId)
    }

    fun createOffer(
        sellerUsername: String = sampleSellerUsername,
        bookAuthor: String = "Jan",
        bookTitle: String = "Jan na drzewie",
        bookCondition: Offer.Condition = Offer.Condition.NEW,
        isbn: String? = "1234567890",
        description: String = "Offer description",
        type: Offer.Type = Offer.Type.EXCHANGE_AND_BUY,
        price: Money? = "10.00".pln(),
        location: String = "Warsaw",
        categoryId: CategoryId = sampleCategoryId,
        shippingMethods: List<CreateOfferCommand.ShippingMethod> = listOf(
            CreateOfferCommand.ShippingMethod(
                id = sampleShippingMethodId,
                price = "10.00".pln()
            )),
        initialStock: Long
    ): Offer {
        val command = CreateOfferCommand(
            book = CreateOfferCommand.Book(
                author = bookAuthor,
                title = bookTitle,
                isbn = isbn,
                condition = bookCondition
            ),
            description = description,
            category = CreateOfferCommand.Category(categoryId),
            type = type,
            images = CreateOfferCommand.Images(
                thumbnail = CreateOfferCommand.Image(TestData.sampleImageUrl),
                allImages = listOf(CreateOfferCommand.Image(TestData.sampleImageUrl))
            ),
            price = price,
            location = location,
            shippingMethods = shippingMethods,
            initialStock = initialStock
        )

        return facade.addOffer(command, sellerUsername)
    }
}
