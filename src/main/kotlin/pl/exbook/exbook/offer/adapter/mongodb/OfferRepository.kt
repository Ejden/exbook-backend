package pl.exbook.exbook.offer.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.common.Cost
import pl.exbook.exbook.common.Currency
import pl.exbook.exbook.offer.domain.Offer

interface OfferRepository : MongoRepository<OfferDocument, String>

@Document(collection = "offers")
data class OfferDocument(
    @Id
    val id: String? = null,
    val book: BookDocument,
    val images: ImagesDocument,
    val description: String?,
    val seller: SellerDocument,
    val type: Offer.Type,
    val cost: CostDocument?,
    val location: String,
    val categories: Collection<CategoryDocument>,
    val shippingMethods: Collection<ShippingMethodDocument>
)

data class BookDocument(
    val author: String,
    val title: String,
    val isbn: Long?,
    val condition: Offer.Condition
)

data class SellerDocument(val id: String)

data class CategoryDocument(val id: String)

data class CostDocument(
    val value: Int,
    val currency: Currency
)

data class ShippingMethodDocument(
    val id: String,
    val cost: CostDocument
)

data class ImagesDocument(
    val thumbnail: ImageDocument?,
    val otherImages: Collection<ImageDocument>
)

data class ImageDocument(val url: String)

class OfferNotFoundException: RuntimeException()

fun OfferDocument.toDomain() = Offer(
    id = Offer.OfferId(this.id!!),
    book = this.book.toDomain(),
    images = this.images.toDomain(),
    description = description,
    type = type,
    seller = this.seller.toDomain(),
    cost = this.cost?.toDomain(),
    location = location,
    categories = categories.map{ it.toDomain() },
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

private fun SellerDocument.toDomain() = Offer.Seller(Offer.SellerId(this.id))

private fun CostDocument.toDomain() = Cost(
    value = this.value,
    currency = this.currency
)

private fun CategoryDocument.toDomain() = Offer.Category(Offer.CategoryId(this.id))

private fun ShippingMethodDocument.toDomain() = Offer.ShippingMethod(
    id = Offer.ShippingMethodId(this.id),
    cost = this.cost.toDomain()
)
