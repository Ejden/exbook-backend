package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ShippingMethodId

data class UpdateOfferCommand(
    val offerId: OfferId,
    val username: String,
    val book: Book,
    val images: Images,
    val description: String,
    val type: Offer.Type,
    val price: Money?,
    val location: String,
    val shippingMethods: List<ShippingMethod>
) {
    init {
        if (book.author.isBlank() || book.author.length >= 256) {
            throw IllegalParameterException("Book author length should be between 1 and 256")
        }

        if (book.title.isBlank() || book.title.length >= 256) {
            throw IllegalParameterException("Book title length should be between 1 and 256")
        }

        if (!((book.isbn == null) || (book.isbn.length == 13) || (book.isbn.length == 10))) {
            throw IllegalParameterException("Book isbn length should be 13 or 10")
        }

        if (description.isBlank() || description.length > 2000) {
            throw IllegalParameterException("Description length should be between 1 and 2000")
        }

        when (type) {
            Offer.Type.BUY_ONLY -> {
                if (price == null) {
                    throw IllegalParameterException("Price should not be null with buy only offer type")
                }
            }
            Offer.Type.EXCHANGE_ONLY -> {
                if (price != null) {
                    throw IllegalParameterException("Price should be null with exchange only offer type")
                }
            }
            Offer.Type.EXCHANGE_AND_BUY -> {
                if (price == null) {
                    throw IllegalParameterException("Price should not be null with buy and exchange offer type")
                }
            }
        }

        if (location.isBlank() || location.length > 256) {
            throw IllegalParameterException("Location length should be between 1 and 256")
        }

        if (shippingMethods.isEmpty()) {
            throw IllegalParameterException("Offer should have at least one shipping method")
        }

        if (shippingMethods.any { it.price < Money.zero(it.price.currency) }) {
            throw IllegalParameterException("Shipping method price cannot be less than 0.00")
        }
    }

    data class Book(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: Offer.Condition
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Image(
        val url: String
    )

    data class ShippingMethod(
        val id: ShippingMethodId,
        val price: Money
    )
}
