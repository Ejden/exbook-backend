package pl.exbook.exbook.offer.adapter.mongodb

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.exbook.exbook.offer.adapter.rest.NewOfferRequest
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferRepository
import pl.exbook.exbook.shared.*
import java.math.BigDecimal

class DatabaseOfferRepository(private val mongoOfferRepository: MongoOfferRepository) : OfferRepository {

    override fun findById(offerId: OfferId): Offer? {
        val offer = mongoOfferRepository.findById(offerId.raw)
        return if (offer.isPresent) offer.get().toDomain() else null
    }

    override fun findAll(pageable: Pageable): Page<Offer> {
        return mongoOfferRepository.findAll(pageable).map { it.toDomain() }
    }

    override fun findAll(): List<Offer> {
        return mongoOfferRepository.findAll().map { it.toDomain() }
    }

    override fun save(newOfferRequest: NewOfferRequest, userId: UserId): Offer {
        return mongoOfferRepository.save(newOfferRequest.toDocument(userId)).toDomain()
    }
}

class OfferNotFoundException(offerId: OfferId) : RuntimeException("Offer with id ${offerId.raw} not found")

fun OfferDocument.toDomain() = Offer(
    id = OfferId(this.id!!),
    book = this.book.toDomain(),
    images = this.images.toDomain(),
    description = description,
    type = type,
    seller = this.seller.toDomain(),
    price = this.cost?.toDomain(),
    location = location,
    category = this.category.toDomain(),
    shippingMethods = shippingMethods.map { it.toDomain() }
)

private fun BookDocument.toDomain() = Offer.Book(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition
)

private fun ImagesDocument.toDomain() = Offer.Images(
    thumbnail = this.thumbnail?.toDomain(),
    otherImages = this.otherImages.map { it.toDomain() }
)

private fun ImageDocument.toDomain() = Offer.Image(this.url)

private fun SellerDocument.toDomain() = Offer.Seller(UserId(this.id))

private fun MoneyDocument.toDomain() = Money(
    amount = BigDecimal(this.amount),
    currency = Currency.valueOf(this.currency)
)

private fun CategoryDocument.toDomain() = Offer.Category(CategoryId(this.id))

private fun ShippingMethodDocument.toDomain() = Offer.ShippingMethod(
    id = ShippingMethodId(this.id),
    money = this.cost.toDomain()
)

private fun NewOfferRequest.toDocument(userId: UserId) = OfferDocument(
    book = BookDocument(
        author = this.book.author,
        title = this.book.title,
        isbn = this.book.isbn,
        condition = this.book.condition
    ),
    images = ImagesDocument(
        thumbnail = null,
        otherImages = emptyList()
    ),
    description = this.description,
    seller = SellerDocument(userId.raw),
    type = this.type,
    cost = if (this.cost == null) null else MoneyDocument(
        amount = this.cost.value,
        currency = this.cost.currency.name
    ),
    location = this.location,
    category = CategoryDocument(this.category),
    shippingMethods = this.shippingMethods.map { ShippingMethodDocument(
        id = it.id,
        cost = MoneyDocument(
            amount = it.cost.value,
            currency = it.cost.currency.name
        )
    )}
)
