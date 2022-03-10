package pl.exbook.exbook.offer.adapter.mongodb

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferRepository
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDocument

@Component
class DatabaseOfferRepository(
    private val mongoOfferRepository: MongoOfferRepository,
) : OfferRepository {

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

    override fun save(offer: Offer): Offer {
        return mongoOfferRepository.save(offer.toDocument()).toDomain()
    }
}

private fun OfferDocument.toDomain() = Offer(
    id = OfferId(this.id),
    versionId = OfferVersionId(this.versionId),
    versionCreationDate = this.versionCreationDate,
    versionExpireDate = this.versionExpireDate,
    book = this.book.toDomain(),
    images = this.images.toDomain(),
    description = this.description,
    type = Offer.Type.valueOf(this.type),
    seller = this.seller.toDomain(),
    price = this.price?.toDomain(),
    location = location,
    category = this.category.toDomain(),
    shippingMethods = shippingMethods.map { it.toDomain() },
    stockId = StockId(this.stockId)
)

private fun BookDocument.toDomain() = Offer.Book(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = Offer.Condition.valueOf(this.condition)
)

private fun ImagesDocument.toDomain() = Offer.Images(
    thumbnail = this.thumbnail?.toDomain(),
    allImages = this.allImages.map { it.toDomain() }
)

private fun ImageDocument.toDomain() = Offer.Image(this.url)

private fun SellerDocument.toDomain() = Offer.Seller(UserId(this.id))

private fun CategoryDocument.toDomain() = Offer.Category(CategoryId(this.id))

private fun ShippingMethodDocument.toDomain() = Offer.ShippingMethod(
    id = ShippingMethodId(this.id),
    price = this.price.toDomain()
)

private fun Offer.toDocument() = OfferDocument(
    id = this.id.raw,
    versionId = this.versionId.raw,
    versionCreationDate = versionCreationDate,
    versionExpireDate = versionExpireDate,
    book = this.book.toDocument(),
    images = this.images.toDocument(),
    description = this.description,
    seller = this.seller.toDocument(),
    type = this.type.name,
    price = this.price?.toDocument(),
    location = this.location,
    category = this.category.toDocument(),
    shippingMethods = this.shippingMethods.map { it.toDocument() },
    stockId = this.stockId.raw
)

private fun Offer.Book.toDocument() = BookDocument(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)

private fun Offer.Images.toDocument() = ImagesDocument(
    thumbnail = this.thumbnail?.toDocument(),
    allImages = this.allImages.map { it.toDocument() }
)

private fun Offer.Image.toDocument() = ImageDocument(
    url = this.url
)

private fun Offer.Seller.toDocument() = SellerDocument(
    id = this.id.raw
)

private fun Offer.Category.toDocument() = CategoryDocument(
    id = this.id.raw
)

private fun Offer.ShippingMethod.toDocument() = ShippingMethodDocument(
    id = this.id.raw,
    price = this.price.toDocument()
)
