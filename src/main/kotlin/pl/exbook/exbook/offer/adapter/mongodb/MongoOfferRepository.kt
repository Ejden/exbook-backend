package pl.exbook.exbook.offer.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.PagingAndSortingRepository
import pl.exbook.exbook.offer.domain.Offer

interface MongoOfferRepository : PagingAndSortingRepository<OfferDocument, String>

@Document(collection = "offers")
data class OfferDocument(
    @Id
    val id: String? = null,
    val book: BookDocument,
    val images: ImagesDocument,
    val description: String?,
    val seller: SellerDocument,
    val type: Offer.Type,
    val cost: MoneyDocument?,
    val location: String,
    val category: CategoryDocument,
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

data class MoneyDocument(
    val amount: String,
    val currency: String
)

data class ShippingMethodDocument(
    val id: String,
    val cost: MoneyDocument
)

data class ImagesDocument(
    val thumbnail: ImageDocument?,
    val otherImages: Collection<ImageDocument>
)

data class ImageDocument(val url: String)
