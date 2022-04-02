package pl.exbook.exbook.mock

import io.mockk.every
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferNotFoundException
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.TestData.sampleAuthor
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleCity
import pl.exbook.exbook.shared.TestData.sampleDescription
import pl.exbook.exbook.shared.TestData.sampleImageUrl
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleOfferVersionId
import pl.exbook.exbook.shared.TestData.sampleSellerId
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.TestData.sampleStockId
import pl.exbook.exbook.shared.TestData.sampleTitle
import pl.exbook.exbook.shared.TestData.tenPln
import pl.exbook.exbook.shared.UserId
import java.time.Instant

class OfferFacadeMocks(private val offerFacade: OfferFacade) {
    fun thereIsNoOfferFor(offerId: OfferId) {
        every { offerFacade.getOffer(offerId) } throws OfferNotFoundException(offerId)
    }

    fun thereIsOffer(init: OfferBuilder.() -> Unit) {
        val mockOffer = OfferBuilder().apply(init).build()
        thereIsOffer(mockOffer)
    }

    fun thereIsOffer(offer: Offer) {
        every { offerFacade.getOffer(offer.id) } returns offer
        every { offerFacade.getOfferVersion(offer.id, any()) } returns offer
    }
}

class OfferBuilder {
    var id: OfferId = sampleOfferId
    var versionId: OfferVersionId = sampleOfferVersionId
    var versionCreationDate: Instant = Instant.EPOCH
    var versionExpireDate: Instant? = null
    var bookAuthor: String = sampleAuthor
    var bookTitle: String = sampleTitle
    var bookCondition: Offer.Condition = Offer.Condition.NEW
    var thumbnailUrl: String? = sampleImageUrl
    var allImagesUrls: List<String> = emptyList()
    var description: String = sampleDescription
    var type: Offer.Type = Offer.Type.EXCHANGE_AND_BUY
    var sellerId: UserId = sampleSellerId
    var price: Money? = tenPln
    var location: String = sampleCity
    var category: Offer.Category = Offer.Category(sampleCategoryId)
    var shippingMethods: MutableList<Offer.ShippingMethod> = mutableListOf(
        Offer.ShippingMethod(
            id = sampleShippingMethodId,
            price = tenPln
        )
    )
    var stockId: StockId = sampleStockId

    private var defaultShippingMethods = true

    fun build() = Offer(
        id = id,
        versionId = versionId,
        versionCreationDate = versionCreationDate,
        versionExpireDate = versionExpireDate,
        book = Offer.Book(
            author = bookAuthor,
            title = bookTitle,
            isbn = "1234567890",
            condition = bookCondition
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

    fun shippingMethod(init: ShippingMethodBuilder.() -> Unit) {
        if (defaultShippingMethods) {
            shippingMethods.clear()
        }
        defaultShippingMethods = false
        shippingMethods.add(ShippingMethodBuilder().apply(init).build())
    }

    class ShippingMethodBuilder {
        var id: ShippingMethodId = sampleShippingMethodId
        var price: Money = tenPln

        fun build() = Offer.ShippingMethod(
            id = id,
            price = price
        )
    }
}

fun offer(init: OfferBuilder.() -> Unit) = OfferBuilder().apply(init).build()
