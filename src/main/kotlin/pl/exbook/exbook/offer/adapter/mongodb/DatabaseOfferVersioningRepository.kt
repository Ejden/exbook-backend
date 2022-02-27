package pl.exbook.exbook.offer.adapter.mongodb

import java.time.Instant
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.offer.domain.OfferVersioningRepository
import pl.exbook.exbook.shared.CategoryId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.dto.toDocument

@Component
class DatabaseOfferVersioningRepository(
    private val repository: MongoOfferVersioningRepository
) : OfferVersioningRepository {
    override fun getOfferVersion(
        offerId: OfferId,
        timestamp: Instant
    ): Offer? = repository.findOfferVersion(offerId.raw, timestamp)?.toDomain()

    override fun getOfferVersion(
        offerVersionId: OfferVersionId
    ): Offer? = repository.findByIdOrNull(offerVersionId.raw)?.toDomain()

    override fun getActiveOfferVersion(
        offerId: OfferId
    ): Offer? = repository.findActiveOfferVersion(offerId.raw)?.toDomain()

    override fun saveOfferVersion(offer: Offer): Offer = repository.save(offer.toDocument()).toDomain()

    override fun insertNewVersion(offer: Offer): Offer = repository.insert(offer.toDocument()).toDomain()
}

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

private fun Offer.toDocument() = OfferVersionDocument(
    versionId = this.versionId.raw,
    offerId = this.id.raw,
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

private fun Offer.Book.toDocument() = OfferVersionDocument.BookDocument(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)

private fun Offer.Images.toDocument() = OfferVersionDocument.ImagesDocument(
    thumbnail = this.thumbnail?.toDocument(),
    otherImages = this.allImages.map { it.toDocument() }
)

private fun Offer.Image.toDocument() = OfferVersionDocument.ImageDocument(
    url = this.url
)

private fun Offer.Seller.toDocument() = OfferVersionDocument.SellerDocument(
    id = this.id.raw
)

private fun Offer.Category.toDocument() = OfferVersionDocument.CategoryDocument(
    id = this.id.raw
)

private fun Offer.ShippingMethod.toDocument() = OfferVersionDocument.ShippingMethodDocument(
    id = this.id.raw,
    price = this.price.toDocument()
)
