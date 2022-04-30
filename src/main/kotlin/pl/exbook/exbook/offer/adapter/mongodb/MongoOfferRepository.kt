package pl.exbook.exbook.offer.adapter.mongodb

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.dto.MoneyDocument

@Repository
interface MongoOfferRepository : PagingAndSortingRepository<OfferDocument, String> {
    fun findAllBySellerId(sellerId: String, pageable: Pageable): Page<OfferDocument>
}

@Document(collection = "offers")
data class OfferDocument(
    @Id
    val id: String,
    val versionId: String,
    val versionCreationDate: Instant,
    val versionExpireDate: Instant?,
    val book: BookDocument,
    val images: ImagesDocument,
    val description: String,
    val seller: SellerDocument,
    val type: String,
    val price: MoneyDocument?,
    val location: String,
    val category: CategoryDocument,
    val shippingMethods: Collection<ShippingMethodDocument>,
    val stockId: String
)

data class BookDocument(
    val author: String,
    val title: String,
    val isbn: String?,
    val condition: String
)

data class SellerDocument(val id: String)

data class CategoryDocument(val id: String)

data class ShippingMethodDocument(
    val id: String,
    val price: MoneyDocument
)

data class ImagesDocument(
    val thumbnail: ImageDocument?,
    val allImages: Collection<ImageDocument>
)

data class ImageDocument(val url: String)
