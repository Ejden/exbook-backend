package pl.exbook.exbook.offer.adapter.mongodb

import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferRepository
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDocument

class DatabaseOfferRepository(
    private val mongoOfferRepository: MongoOfferRepository,
    private val mongoOfferVersioningRepository: MongoOfferVersioningRepository
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
        mongoOfferVersioningRepository.findActiveOfferVersion(offer.id.raw)?.let {
            val deactivatedOfferVersion = it.toDomain().deactivate(offer.versionCreationDate).toVersionedDocument()
            mongoOfferVersioningRepository.save(deactivatedOfferVersion)
        }
        mongoOfferVersioningRepository.insert(offer.toVersionedDocument())
        return mongoOfferRepository.save(offer.toDocument()).toDomain()
    }

    override fun getOfferVersionFrom(
        offerId: OfferId,
        timestamp: Instant
    ): Offer? = mongoOfferVersioningRepository.findOfferVersion(offerId.raw, timestamp)?.toDomain()
}

class OfferNotFoundException(offerId: OfferId) : RuntimeException("Offer with id ${offerId.raw} not found")

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
    allImages = this.otherImages.map { it.toDomain() }
)

private fun ImageDocument.toDomain() = Offer.Image(this.url)

private fun SellerDocument.toDomain() = Offer.Seller(UserId(this.id))

private fun CategoryDocument.toDomain() = Offer.Category(CategoryId(this.id))

private fun ShippingMethodDocument.toDomain() = Offer.ShippingMethod(
    id = ShippingMethodId(this.id),
    price = this.price.toDomain()
)

private fun OfferVersionDocument.toDomain() = Offer(
    id = OfferId(this.offerId),
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

private fun OfferVersionDocument.BookDocument.toDomain() = Offer.Book(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = Offer.Condition.valueOf(this.condition)
)

private fun OfferVersionDocument.ImagesDocument.toDomain() = Offer.Images(
    thumbnail = this.thumbnail?.toDomain(),
    allImages = this.otherImages.map { it.toDomain() }
)

private fun OfferVersionDocument.ImageDocument.toDomain() = Offer.Image(this.url)

private fun OfferVersionDocument.SellerDocument.toDomain() = Offer.Seller(UserId(this.id))

private fun OfferVersionDocument.CategoryDocument.toDomain() = Offer.Category(CategoryId(this.id))

private fun OfferVersionDocument.ShippingMethodDocument.toDomain() = Offer.ShippingMethod(
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
    otherImages = this.allImages.map { it.toDocument() }
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

private fun Offer.toVersionedDocument() = OfferVersionDocument(
    versionId = this.versionId.raw,
    offerId = this.id.raw,
    versionCreationDate = versionCreationDate,
    versionExpireDate = versionExpireDate,
    book = this.book.toVersionedDocument(),
    images = this.images.toVersionedDocument(),
    description = this.description,
    seller = this.seller.toVersionedDocument(),
    type = this.type.name,
    price = this.price?.toDocument(),
    location = this.location,
    category = this.category.toVersionedDocument(),
    shippingMethods = this.shippingMethods.map { it.toVersionedDocument() },
    stockId = this.stockId.raw
)

private fun Offer.Book.toVersionedDocument() = OfferVersionDocument.BookDocument(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)

private fun Offer.Images.toVersionedDocument() = OfferVersionDocument.ImagesDocument(
    thumbnail = this.thumbnail?.toVersionedDocument(),
    otherImages = this.allImages.map { it.toVersionedDocument() }
)

private fun Offer.Image.toVersionedDocument() = OfferVersionDocument.ImageDocument(
    url = this.url
)

private fun Offer.Seller.toVersionedDocument() = OfferVersionDocument.SellerDocument(
    id = this.id.raw
)

private fun Offer.Category.toVersionedDocument() = OfferVersionDocument.CategoryDocument(
    id = this.id.raw
)

private fun Offer.ShippingMethod.toVersionedDocument() = OfferVersionDocument.ShippingMethodDocument(
    id = this.id.raw,
    price = this.price.toDocument()
)
