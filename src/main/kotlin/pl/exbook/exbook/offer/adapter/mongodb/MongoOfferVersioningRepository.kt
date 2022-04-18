package pl.exbook.exbook.offer.adapter.mongodb

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import pl.exbook.exbook.shared.dto.MoneyDocument

@Repository
interface MongoOfferVersioningRepository : MongoRepository<OfferVersionDocument, String> {
    @Query("{ offerId : ?0, versionExpireDate : null }")
    fun findActiveOfferVersion(offerId: String): OfferVersionDocument?

    @Query("{ offerId : ?0, versionCreationDate: { \$lte: ?1 }, \$or: [ { versionExpireDate: { \$gt: ?1 } }, { versionExpireDate: null } ] }")
    fun findOfferVersion(offerId: String, versionFrom: Instant): OfferVersionDocument?
}

@Document(collection = "offersVersioning")
data class OfferVersionDocument(
    @field:Id
    val versionId: String,
    val offerId: String,
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
) {
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
}
