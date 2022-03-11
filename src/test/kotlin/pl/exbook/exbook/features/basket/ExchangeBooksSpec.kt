package pl.exbook.exbook.features.basket

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import pl.exbook.exbook.ability.BasketDomainAbility
import pl.exbook.exbook.basket.domain.AddExchangeBookToBasketCommand
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.basket.domain.BasketValidationException
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.TestData.otherSampleOfferId
import pl.exbook.exbook.shared.TestData.otherSampleSellerId
import pl.exbook.exbook.shared.TestData.otherSampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleBuyerId
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleExchangeBookId
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername

class ExchangeBooksSpec : ShouldSpec({
    val domain = BasketDomainAbility()

    should("add exchange book to items group when there is only one item group") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )

        val command = AddExchangeBookToBasketCommand(
            username = sampleBuyerUsername,
            sellerId = sampleSellerId,
            book = AddExchangeBookToBasketCommand.ExchangeBook(
                author = "Tom",
                title = "Riddle",
                isbn = null,
                condition = Offer.Condition.NEW,
                quantity = 1
            )
        )

        // when
        domain.facade.addExchangeBookToBasket(command)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups.entries shouldHaveSize 1

        // and
        val itemGroup = basket.itemsGroups.entries.toList()[0]
        itemGroup.value.exchangeBooks shouldHaveSize 1

        // and
        val book = itemGroup.value.exchangeBooks[0]
        book.author shouldBe "Tom"
        book.title shouldBe "Riddle"
        book.isbn shouldBe null
        book.condition shouldBe Offer.Condition.NEW
        book.quantity shouldBeExactly 1
    }

    should("add exchange book to correct items group when there are more than one exchange group from different sellers") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsUser(userId = otherSampleSellerId, username = otherSampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = otherSampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = otherSampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )

        val command = AddExchangeBookToBasketCommand(
            username = sampleBuyerUsername,
            sellerId = otherSampleSellerId,
            book = AddExchangeBookToBasketCommand.ExchangeBook(
                author = "Tom",
                title = "Riddle",
                isbn = null,
                condition = Offer.Condition.NEW,
                quantity = 1
            )
        )

        // when
        domain.facade.addExchangeBookToBasket(command)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups.entries shouldHaveSize 2

        // and
        val itemGroup = basket.itemsGroups.entries.toList()[1]
        itemGroup.value.exchangeBooks shouldHaveSize 1

        // and
        val book = itemGroup.value.exchangeBooks[0]
        book.author shouldBe "Tom"
        book.title shouldBe "Riddle"
        book.isbn shouldBe null
        book.condition shouldBe Offer.Condition.NEW
        book.quantity shouldBeExactly 1
    }

    should("add exchange book to correct items group when there are more than group from the same seller") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.BUY
            )
        )
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = otherSampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )

        val command = AddExchangeBookToBasketCommand(
            username = sampleBuyerUsername,
            sellerId = sampleSellerId,
            book = AddExchangeBookToBasketCommand.ExchangeBook(
                author = "Tom",
                title = "Riddle",
                isbn = null,
                condition = Offer.Condition.NEW,
                quantity = 1
            )
        )

        // when
        domain.facade.addExchangeBookToBasket(command)
        val basket = domain.facade.getUserBasket(sampleBuyerId)

        // then
        basket.itemsGroups.entries shouldHaveSize 2

        // and
        val itemGroup = basket.itemsGroups.entries.toList()[1]
        itemGroup.value.exchangeBooks shouldHaveSize 1

        // and
        val book = itemGroup.value.exchangeBooks[0]
        book.author shouldBe "Tom"
        book.title shouldBe "Riddle"
        book.isbn shouldBe null
        book.condition shouldBe Offer.Condition.NEW
        book.quantity shouldBeExactly 1
    }

    should("throw an error when there is no matching group for seller when trying to add exchange book") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.BUY
            )
        )
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = otherSampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )

        val command = AddExchangeBookToBasketCommand(
            username = sampleBuyerUsername,
            sellerId = otherSampleSellerId,
            book = AddExchangeBookToBasketCommand.ExchangeBook(
                author = "Tom",
                title = "Riddle",
                isbn = null,
                condition = Offer.Condition.NEW,
                quantity = 1
            )
        )

        // expect
        shouldThrowExactly<BasketValidationException> {
            domain.facade.addExchangeBookToBasket(command)
        }
    }

    should("throw an error when there is no matching group for order type when trying to add exchange book") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.BUY
            )
        )

        val command = AddExchangeBookToBasketCommand(
            username = sampleBuyerUsername,
            sellerId = sampleSellerId,
            book = AddExchangeBookToBasketCommand.ExchangeBook(
                author = "Tom",
                title = "Riddle",
                isbn = null,
                condition = Offer.Condition.NEW,
                quantity = 1
            )
        )

        // expect
        shouldThrowExactly<BasketValidationException> {
            domain.facade.addExchangeBookToBasket(command)
        }
    }

    context("throw an error when trying to add exchange book with incorrect data") {
        withData(
            ExchangeBookTestCase(username = ""),
            ExchangeBookTestCase(username = " "),
            ExchangeBookTestCase(author = ""),
            ExchangeBookTestCase(author = " "),
            ExchangeBookTestCase(title = ""),
            ExchangeBookTestCase(title = " "),
            ExchangeBookTestCase(isbn = "123456"),
            ExchangeBookTestCase(isbn = "123456789"),
            ExchangeBookTestCase(isbn = "12345678901"),
            ExchangeBookTestCase(isbn = "123456789012"),
            ExchangeBookTestCase(isbn = "12345678901234"),
            ExchangeBookTestCase(quantity = 0),
            ExchangeBookTestCase(quantity = -1),
            ExchangeBookTestCase(quantity = -2),
        ) { (username, author, title, isbn, quantity) ->
            // expect
            shouldThrowExactly<IllegalParameterException> {
                AddExchangeBookToBasketCommand(
                    username = username,
                    sellerId = sampleSellerId,
                    book = AddExchangeBookToBasketCommand.ExchangeBook(
                        author = author,
                        title = title,
                        isbn = isbn,
                        condition = Offer.Condition.NEW,
                        quantity = quantity
                    )
                )
            }
        }
    }

    should("remove exchange book from correct items group") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsUser(userId = otherSampleSellerId, username = otherSampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.thereIsOffer(offerId = otherSampleOfferId, sellerId = otherSampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.BUY
            )
        )
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = otherSampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )
        val result1 = domain.facade.addExchangeBookToBasket(
            AddExchangeBookToBasketCommand(
                username = sampleBuyerUsername,
                sellerId = otherSampleSellerId,
                book = AddExchangeBookToBasketCommand.ExchangeBook(
                    author = "Tom",
                    title = "Riddle",
                    isbn = null,
                    condition = Offer.Condition.NEW,
                    quantity = 1
                )
            )
        )
        domain.facade.addExchangeBookToBasket(
            AddExchangeBookToBasketCommand(
                username = sampleBuyerUsername,
                sellerId = otherSampleSellerId,
                book = AddExchangeBookToBasketCommand.ExchangeBook(
                    author = "Inne",
                    title = "Riddle3",
                    isbn = null,
                    condition = Offer.Condition.NEW,
                    quantity = 2
                )
            )
        )
        val book = result1.itemsGroups.toList()[2].second.exchangeBooks[0]

        // then
        var basket = domain.facade.getUserBasket(sampleBuyerId)
        basket.itemsGroups.toList()[2].second.exchangeBooks shouldHaveSize 2

        // when
        domain.facade.removeExchangeBookFromBasket(sampleBuyerUsername, otherSampleSellerId, book.id)

        // then
        basket = domain.facade.getUserBasket(sampleBuyerId)
        val exchangeBooks = basket.itemsGroups.toList()[2].second.exchangeBooks
        exchangeBooks shouldHaveSize 1
        exchangeBooks[0].id shouldNotBe book.id
    }

    should("not throw error when trying to remove non existing book from items group") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)
        domain.facade.addItemToBasket(
            AddItemToBasketCommand(
                username = sampleBuyerUsername,
                offerId = sampleOfferId,
                quantity = 1,
                orderType = OrderType.EXCHANGE
            )
        )

        // expect
        shouldNotThrowAny {
            domain.facade.removeExchangeBookFromBasket(sampleBuyerUsername, sampleSellerId, sampleExchangeBookId)
        }
    }

    should("not throw error when trying to remove exchange book from non existing items group") {
        // given
        domain.thereIsUser(userId = sampleBuyerId, username = sampleBuyerUsername)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsOffer(offerId = sampleOfferId, sellerId = sampleSellerId)

        // expect
        shouldNotThrowAny {
            domain.facade.removeExchangeBookFromBasket(sampleBuyerUsername, sampleSellerId, sampleExchangeBookId)
        }
    }
})

private data class ExchangeBookTestCase(
    val username: String = "Tom",
    val author: String = "Riddle",
    val title: String = "Some title",
    val isbn: String? = null,
    val quantity: Int = 1
)
