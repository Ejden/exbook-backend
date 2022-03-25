package pl.exbook.exbook.features.offer

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.OfferDomainAbility
import pl.exbook.exbook.offer.domain.CreateOfferCommand
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.pln
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.TestData.otherSampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleStockId
import pl.exbook.exbook.shared.ValidationException
import pl.exbook.exbook.user.domain.UserNotFoundException

class OfferCreationSpec : ShouldSpec({
    val domain = OfferDomainAbility()

    context("create offer") {
        withData(
            CreateOfferTestCase("1234567890", Offer.Type.BUY_ONLY, "10.00".pln(), 1),
            CreateOfferTestCase("1234567890", Offer.Type.EXCHANGE_AND_BUY, "10.00".pln(), 1),
            CreateOfferTestCase("1234567890", Offer.Type.EXCHANGE_ONLY, null, 1),
            CreateOfferTestCase("1234567890123", Offer.Type.BUY_ONLY, "10.00".pln(), 1)
        ) { (isbn, offerType, price, initialStock) ->
            // given
            domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = initialStock)
            domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsCategory(categoryId = sampleCategoryId)

            val command = CreateOfferCommand(
                book = CreateOfferCommand.Book(
                    author = "Tom",
                    title = "Riddle",
                    isbn = isbn,
                    condition = Offer.Condition.NEW
                ),
                description = "Offer description",
                category = CreateOfferCommand.Category(sampleCategoryId),
                type = offerType,
                price = price,
                location = "Warsaw",
                shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "10.00".pln())),
                initialStock = initialStock
            )

            // when
            val result = domain.facade.addOffer(command, sampleSellerUsername)

            // then
            result.book.author shouldBe "Tom"
            result.book.title shouldBe "Riddle"
            result.book.isbn shouldBe isbn
            result.book.condition shouldBe Offer.Condition.NEW
            result.description shouldBe "Offer description"
            result.category.id shouldBe sampleCategoryId
            result.type shouldBe offerType
            if (price == null) {
                result.price.shouldBeNull()
            } else {
                result.price!! shouldBeEqualComparingTo price
            }
            result.location shouldBe "Warsaw"
            result.shippingMethods shouldHaveSize 1
            result.shippingMethods[0].id shouldBe sampleShippingMethodId
            result.shippingMethods[0].price shouldBeEqualComparingTo "10.00".pln()
        }
    }

    should("save new offer version when creating offer") {
        // given
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = 100)
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsCategory(categoryId = sampleCategoryId)

        val command = CreateOfferCommand(
            book = CreateOfferCommand.Book(
                author = "Tom",
                title = "Riddle",
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            description = "Offer description",
            category = CreateOfferCommand.Category(sampleCategoryId),
            type = Offer.Type.BUY_ONLY,
            price = "10.00".pln(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "10.00".pln())),
            initialStock = 100
        )

        // when
        val result = domain.facade.addOffer(command, sampleSellerUsername)
        val offerVersion = domain.facade.getOfferVersion(result.versionId)

        result.id shouldBe offerVersion.id
        result.versionId shouldBe offerVersion.versionId
    }

    context("throw an exception while validation create offer command") {
        withData(
            CreateOfferCmdTestCase(initialStock = -1),
            CreateOfferCmdTestCase(initialStock = 0),
            CreateOfferCmdTestCase(bookAuthor = ""),
            CreateOfferCmdTestCase(bookAuthor = " "),
            CreateOfferCmdTestCase(bookTitle = ""),
            CreateOfferCmdTestCase(bookTitle = " "),
            CreateOfferCmdTestCase(isbn = ""),
            CreateOfferCmdTestCase(isbn = "12"),
            CreateOfferCmdTestCase(isbn = "123456789"),
            CreateOfferCmdTestCase(isbn = "123456789012"),
            CreateOfferCmdTestCase(isbn = "12345678901234"),
            CreateOfferCmdTestCase(description = ""),
            CreateOfferCmdTestCase(description =  " "),
            CreateOfferCmdTestCase(offerType = Offer.Type.EXCHANGE_AND_BUY, price = null),
            CreateOfferCmdTestCase(offerType = Offer.Type.BUY_ONLY, price = null),
            CreateOfferCmdTestCase(offerType = Offer.Type.EXCHANGE_ONLY, price = "10.00".pln()),
            CreateOfferCmdTestCase(location = ""),
            CreateOfferCmdTestCase(location = " "),
            CreateOfferCmdTestCase(shippingMethods = emptyList()),
            CreateOfferCmdTestCase(shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "-0.01".pln()))),
            CreateOfferCmdTestCase(shippingMethods = listOf(
                CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "0.00".pln()),
                CreateOfferCommand.ShippingMethod(otherSampleShippingMethodId, "-0.01".pln())
            )),
        ) { (initialStock, bookAuthor, bookTitle, isbn, description, offerType, price, location, shippingMethods) ->
            // expect
            shouldThrowExactly<IllegalParameterException> {
                CreateOfferCommand(
                    book = CreateOfferCommand.Book(
                        author = bookAuthor,
                        title = bookTitle,
                        isbn = isbn,
                        condition = Offer.Condition.NEW
                    ),
                    description = description,
                    category = CreateOfferCommand.Category(sampleCategoryId),
                    type = offerType,
                    price = price,
                    location = location,
                    shippingMethods = shippingMethods,
                    initialStock = initialStock
                )
            }
        }
    }

    should("throw an error when trying to add offer with non existing shipping method") {
        // given
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsNoShippingMethod(shippingMethodId = sampleShippingMethodId)

        val command = CreateOfferCommand(
            book = CreateOfferCommand.Book(
                author = "Tom",
                title = "Riddle",
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            description = "Offer description",
            category = CreateOfferCommand.Category(sampleCategoryId),
            type = Offer.Type.EXCHANGE_AND_BUY,
            price = "10.00".pln(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "10.00".pln())),
            initialStock = 100
        )

        // expect
        shouldThrowExactly<ValidationException> {
            domain.facade.addOffer(command, sampleSellerUsername)
        }
    }

    should("throw an exception when trying to add offer for non existing user") {
        // given
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId)
        domain.thereIsNoUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.thereIsCategory(categoryId = sampleCategoryId)

        val command = CreateOfferCommand(
            book = CreateOfferCommand.Book(
                author = "Tom",
                title = "Riddle",
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            description = "Offer description",
            category = CreateOfferCommand.Category(sampleCategoryId),
            type = Offer.Type.EXCHANGE_AND_BUY,
            price = "10.00".pln(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "10.00".pln())),
            initialStock = 100
        )

        // expect
        shouldThrowExactly<UserNotFoundException> {
            domain.facade.addOffer(command, sampleSellerUsername)
        }
    }

    should("throw an exception when trying to add offer with non existing cateogory") {
        // given
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId)
        domain.thereIsNoUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.thereIsNoCategory(categoryId = sampleCategoryId)

        val command = CreateOfferCommand(
            book = CreateOfferCommand.Book(
                author = "Tom",
                title = "Riddle",
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            description = "Offer description",
            category = CreateOfferCommand.Category(sampleCategoryId),
            type = Offer.Type.EXCHANGE_AND_BUY,
            price = "10.00".pln(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "10.00".pln())),
            initialStock = 100
        )

        // expect
        shouldThrowExactly<ValidationException> {
            domain.facade.addOffer(command, sampleSellerUsername)
        }
    }
})

private data class CreateOfferTestCase(
    val isbn: String?,
    val type: Offer.Type,
    val price: Money?,
    val initialStock: Long
)

private data class CreateOfferCmdTestCase(
    val initialStock: Long = 1,
    val bookAuthor: String = "Tom",
    val bookTitle: String = "Riddle",
    val isbn: String = "1234567890123",
    val description: String = "description",
    val offerType: Offer.Type = Offer.Type.EXCHANGE_AND_BUY,
    val price: Money? = "10.00".pln(),
    val location: String = "Warsaw",
    val shippingMethods: List<CreateOfferCommand.ShippingMethod> = listOf(
        CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "20.00".pln())
    )
)
