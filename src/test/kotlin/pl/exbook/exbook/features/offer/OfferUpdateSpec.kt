package pl.exbook.exbook.features.offer

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.OfferDomainAbility
import pl.exbook.exbook.offer.domain.CreateOfferCommand
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferChangeValidationException
import pl.exbook.exbook.offer.domain.OfferNotFoundException
import pl.exbook.exbook.offer.domain.UpdateOfferCommand
import pl.exbook.exbook.pln
import pl.exbook.exbook.security.domain.UnauthorizedException
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.TestData.otherSampleSellerId
import pl.exbook.exbook.shared.TestData.otherSampleSellerUsername
import pl.exbook.exbook.shared.TestData.otherSampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleImageUrl
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleStockId
import pl.exbook.exbook.user.domain.UserNotFoundException

class OfferUpdateSpec : ShouldSpec({
    val domain = OfferDomainAbility()

    context("update offer") {
        withData(
            UpdateOfferTestCase("1234567890", Offer.Type.BUY_ONLY, Offer.Type.BUY_ONLY, "10.00".pln(), "11.00".pln(), Offer.Condition.NEW, Offer.Condition.LIGHTLY_USED),
            UpdateOfferTestCase("1234567890123", Offer.Type.BUY_ONLY, Offer.Type.BUY_ONLY, "10.00".pln(), "10.00".pln(), Offer.Condition.NEW, Offer.Condition.LIGHTLY_USED),
            UpdateOfferTestCase("1234567891", Offer.Type.BUY_ONLY, Offer.Type.BUY_ONLY, "10.00".pln(), "10.00".pln(), Offer.Condition.NEW, Offer.Condition.LIGHTLY_USED),
            UpdateOfferTestCase("1234567890", Offer.Type.BUY_ONLY, Offer.Type.EXCHANGE_AND_BUY, "10.00".pln(), "11.00".pln(), Offer.Condition.LIGHTLY_USED, Offer.Condition.LIGHTLY_USED),
            UpdateOfferTestCase("1234567890", Offer.Type.BUY_ONLY, Offer.Type.EXCHANGE_ONLY, "10.00".pln(), null, Offer.Condition.LIGHTLY_USED, Offer.Condition.LIGHTLY_USED),
            UpdateOfferTestCase("1234567890", Offer.Type.EXCHANGE_ONLY, Offer.Type.BUY_ONLY, null, "100.99".pln(), Offer.Condition.LIGHTLY_USED, Offer.Condition.LIGHTLY_USED),
            UpdateOfferTestCase("1234567890", Offer.Type.EXCHANGE_ONLY, Offer.Type.EXCHANGE_AND_BUY, null, "100.99".pln(), Offer.Condition.LIGHTLY_USED, Offer.Condition.LIGHTLY_USED),
        ) { (isbn, oldType, type, oldPrice, price, oldBookCondition, bookCondition) ->
            // given
            domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
            domain.thereIsShippingMethod(shippingMethodId = otherSampleShippingMethodId)
            domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = 100)
            domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
            domain.thereIsCategory(categoryId = sampleCategoryId)
            val oldOffer = domain.createOffer(
                sellerUsername = sampleSellerUsername,
                bookAuthor = "standard",
                bookTitle = "standard",
                bookCondition = oldBookCondition,
                isbn = "1234567890",
                description = "standard",
                type = oldType,
                price = oldPrice,
                location = "standard",
                categoryId = sampleCategoryId,
                shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "1.99".pln())),
                initialStock = 100
            )

            val updateCommand = UpdateOfferCommand(
                offerId = oldOffer.id,
                username = sampleSellerUsername,
                book = UpdateOfferCommand.Book(
                    author = "updated-author",
                    title = "updated-title",
                    isbn = isbn,
                    condition = bookCondition
                ),
                images = UpdateOfferCommand.Images(
                    thumbnail = UpdateOfferCommand.Image(sampleImageUrl),
                    allImages = listOf(UpdateOfferCommand.Image(sampleImageUrl))
                ),
                description = "updated-description",
                type = type,
                price = price,
                location = "updated-location",
                shippingMethods = listOf(UpdateOfferCommand.ShippingMethod(otherSampleShippingMethodId, "2.00".pln()))
            )

            // when
            val result = domain.facade.updateOffer(updateCommand)

            // then
            result.book.author shouldBe "updated-author"
            result.book.title shouldBe "updated-title"
            result.book.isbn shouldBe isbn
            result.book.condition shouldBe bookCondition
            result.images.thumbnail.shouldNotBeNull()
            result.images.thumbnail?.url shouldBe sampleImageUrl
            result.images.allImages shouldHaveSize 1
            result.images.allImages[0].url shouldBe sampleImageUrl
            result.description shouldBe "updated-description"
            result.type shouldBe type
            if (price == null) {
                result.price.shouldBeNull()
            } else {
                result.price!! shouldBeEqualComparingTo price
            }
            result.location shouldBe "updated-location"
            result.shippingMethods shouldHaveSize 1
            result.shippingMethods[0].id shouldBe otherSampleShippingMethodId
            result.shippingMethods[0].price shouldBeEqualComparingTo "2.00".pln()
        }
    }

    context("throw an exception while update offer command is not valid") {
        withData(
            UpdateCmdTestCase(bookAuthor = ""),
            UpdateCmdTestCase(bookAuthor = " "),
            UpdateCmdTestCase(bookTitle = ""),
            UpdateCmdTestCase(bookTitle = " "),
            UpdateCmdTestCase(isbn = ""),
            UpdateCmdTestCase(isbn = "12"),
            UpdateCmdTestCase(isbn = "123456789"),
            UpdateCmdTestCase(isbn = "123456789012"),
            UpdateCmdTestCase(isbn = "12345678901234"),
            UpdateCmdTestCase(description = ""),
            UpdateCmdTestCase(description =  " "),
            UpdateCmdTestCase(offerType = Offer.Type.EXCHANGE_AND_BUY, price = null),
            UpdateCmdTestCase(offerType = Offer.Type.BUY_ONLY, price = null),
            UpdateCmdTestCase(offerType = Offer.Type.EXCHANGE_ONLY, price = "10.00".pln()),
            UpdateCmdTestCase(location = ""),
            UpdateCmdTestCase(location = " "),
            UpdateCmdTestCase(shippingMethods = emptyList()),
            UpdateCmdTestCase(shippingMethods = listOf(UpdateOfferCommand.ShippingMethod(sampleShippingMethodId, "-0.01".pln()))),
            UpdateCmdTestCase(shippingMethods = listOf(
                UpdateOfferCommand.ShippingMethod(sampleShippingMethodId, "0.00".pln()),
                UpdateOfferCommand.ShippingMethod(otherSampleShippingMethodId, "-0.01".pln())
            ))
        ) { (bookAuthor, bookTitle, isbn, description, offerType, price, location, shippingMethods) ->
            // expect
            shouldThrowExactly<IllegalParameterException> {
                UpdateOfferCommand(
                    offerId = sampleOfferId,
                    username = sampleSellerUsername,
                    book = UpdateOfferCommand.Book(
                        author = bookAuthor,
                        title = bookTitle,
                        isbn = isbn,
                        condition = Offer.Condition.NEW
                    ),
                    description = description,
                    type = offerType,
                    price = price,
                    location = location,
                    shippingMethods = shippingMethods,
                    images = UpdateOfferCommand.Images(null, emptyList())
                )
            }
        }
    }

    should("throw an error when trying to update non existing offer") {
        // given
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.thereIsShippingMethod(shippingMethodId = otherSampleShippingMethodId)
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = 100)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)

        val updateCommand = UpdateOfferCommand(
            offerId = sampleOfferId,
            username = sampleSellerUsername,
            book = UpdateOfferCommand.Book(
                author = "updated-author",
                title = "updated-title",
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            images = UpdateOfferCommand.Images(
                thumbnail = UpdateOfferCommand.Image(sampleImageUrl),
                allImages = listOf(UpdateOfferCommand.Image(sampleImageUrl))
            ),
            description = "updated-description",
            type = Offer.Type.BUY_ONLY,
            price = "10.00".pln(),
            location = "updated-location",
            shippingMethods = listOf(UpdateOfferCommand.ShippingMethod(otherSampleShippingMethodId, "2.00".pln()))
        )

        // expect
        shouldThrowExactly<OfferNotFoundException> {
            domain.facade.updateOffer(updateCommand)
        }
    }

    should("throw an error when seller doesn't exists") {
        // given
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.thereIsShippingMethod(shippingMethodId = otherSampleShippingMethodId)
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = 100)
        domain.thereIsNoUser(userId = sampleSellerId, username = sampleSellerUsername)

        val updateCommand = UpdateOfferCommand(
            offerId = sampleOfferId,
            username = sampleSellerUsername,
            book = UpdateOfferCommand.Book(
                author = "updated-author",
                title = "updated-title",
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            images = UpdateOfferCommand.Images(
                thumbnail = UpdateOfferCommand.Image(sampleImageUrl),
                allImages = listOf(UpdateOfferCommand.Image(sampleImageUrl))
            ),
            description = "updated-description",
            type = Offer.Type.BUY_ONLY,
            price = "10.00".pln(),
            location = "updated-location",
            shippingMethods = listOf(UpdateOfferCommand.ShippingMethod(otherSampleShippingMethodId, "2.00".pln()))
        )

        // expect
        shouldThrowExactly<UserNotFoundException> {
            domain.facade.updateOffer(updateCommand)
        }
    }

    should("throw an error when trying to update offer that doesn't belong to seller") {
        // given
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.thereIsShippingMethod(shippingMethodId = otherSampleShippingMethodId)
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = 100)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsUser(userId = otherSampleSellerId, username = otherSampleSellerUsername)
        domain.thereIsCategory(categoryId = sampleCategoryId)
        val oldOffer = domain.createOffer(
            sellerUsername = sampleSellerUsername,
            bookAuthor = "standard",
            bookTitle = "standard",
            bookCondition = Offer.Condition.NEW,
            isbn = "1234567890",
            description = "standard",
            type = Offer.Type.BUY_ONLY,
            price = "10.00".pln(),
            location = "standard",
            categoryId = sampleCategoryId,
            shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "1.99".pln())),
            initialStock = 100
        )

        val updateCommand = UpdateOfferCommand(
            offerId = oldOffer.id,
            username = otherSampleSellerUsername,
            book = UpdateOfferCommand.Book(
                author = "updated-author",
                title = "updated-title",
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            images = UpdateOfferCommand.Images(
                thumbnail = UpdateOfferCommand.Image(sampleImageUrl),
                allImages = listOf(UpdateOfferCommand.Image(sampleImageUrl))
            ),
            description = "updated-description",
            type = Offer.Type.BUY_ONLY,
            price = "10.00".pln(),
            location = "updated-location",
            shippingMethods = listOf(UpdateOfferCommand.ShippingMethod(otherSampleShippingMethodId, "2.00".pln()))
        )

        // expect
        shouldThrowExactly<UnauthorizedException> {
            domain.facade.updateOffer(updateCommand)
        }
    }

    should("throw an error when trying to update offer with non existing shipping method") {
        // given
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.thereIsNoShippingMethod(shippingMethodId = otherSampleShippingMethodId)
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = 100)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsCategory(categoryId = sampleCategoryId)
        val oldOffer = domain.createOffer(
            sellerUsername = sampleSellerUsername,
            bookAuthor = "standard",
            bookTitle = "standard",
            bookCondition = Offer.Condition.NEW,
            isbn = "1234567890",
            description = "standard",
            type = Offer.Type.BUY_ONLY,
            price = "10.00".pln(),
            location = "standard",
            categoryId = sampleCategoryId,
            shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "1.99".pln())),
            initialStock = 100
        )

        val updateCommand = UpdateOfferCommand(
            offerId = oldOffer.id,
            username = sampleSellerUsername,
            book = UpdateOfferCommand.Book(
                author = "updated-author",
                title = "updated-title",
                isbn = "1234567890",
                condition = Offer.Condition.NEW
            ),
            images = UpdateOfferCommand.Images(
                thumbnail = UpdateOfferCommand.Image(sampleImageUrl),
                allImages = listOf(UpdateOfferCommand.Image(sampleImageUrl))
            ),
            description = "updated-description",
            type = Offer.Type.BUY_ONLY,
            price = "10.00".pln(),
            location = "updated-location",
            shippingMethods = listOf(UpdateOfferCommand.ShippingMethod(otherSampleShippingMethodId, "2.00".pln()))
        )

        // expect
        shouldThrowExactly<OfferChangeValidationException> {
            domain.facade.updateOffer(updateCommand)
        }
    }
})

private data class UpdateOfferTestCase(
    val isbn: String?,
    val oldType: Offer.Type,
    val type: Offer.Type,
    val oldPrice: Money?,
    val price: Money?,
    val oldBookCondition: Offer.Condition,
    val bookCondition: Offer.Condition
)

private data class UpdateCmdTestCase(
    val bookAuthor: String = "Tom",
    val bookTitle: String = "Riddle",
    val isbn: String = "1234567890123",
    val description: String = "description",
    val offerType: Offer.Type = Offer.Type.EXCHANGE_AND_BUY,
    val price: Money? = "10.00".pln(),
    val location: String = "Warsaw",
    val shippingMethods: List<UpdateOfferCommand.ShippingMethod> = listOf(
        UpdateOfferCommand.ShippingMethod(sampleShippingMethodId, "20.00".pln())
    )
)
