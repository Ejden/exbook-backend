package pl.exbook.exbook.features.offer

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.OfferDomainAbility
import pl.exbook.exbook.offer.domain.CreateOfferCommand
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferNotFoundException
import pl.exbook.exbook.pln
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleStockId

class OfferFacadeSpec : ShouldSpec({
    val domain = OfferDomainAbility()

    should("get actual offer version") {
        // given
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = 100)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)
        domain.thereIsCategory(categoryId = sampleCategoryId)
        val offer = domain.createOffer(
            sellerUsername = sampleSellerUsername,
            bookAuthor = "standard",
            bookTitle = "standard",
            bookCondition = Offer.Condition.BAD,
            isbn = "1234567890",
            description = "standard",
            type = Offer.Type.BUY_ONLY,
            price = "19.99".pln(),
            location = "standard",
            categoryId = sampleCategoryId,
            shippingMethods = listOf(CreateOfferCommand.ShippingMethod(sampleShippingMethodId, "1.99".pln())),
            initialStock = 100
        )

        // when
        val actualOffer = domain.facade.getOffer(offer.id)

        // then
        actualOffer.versionId shouldBe offer.versionId
    }

    should("throw an exception when offer was not found") {
        // given
        domain.thereIsShippingMethod(shippingMethodId = sampleShippingMethodId)
        domain.stockFacadeWillCreateStockForOffer(stockId = sampleStockId, initialStock = 100)
        domain.thereIsUser(userId = sampleSellerId, username = sampleSellerUsername)

        // expect
        shouldThrowExactly<OfferNotFoundException> {
            domain.facade.getOffer(sampleOfferId)
        }
    }
})
