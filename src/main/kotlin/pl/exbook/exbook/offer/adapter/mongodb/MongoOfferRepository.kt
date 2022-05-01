package pl.exbook.exbook.offer.adapter.mongodb

import java.math.BigDecimal
import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import pl.exbook.exbook.shared.dto.MoneyDocument

@Repository
interface MongoOfferRepository : PagingAndSortingRepository<OfferDocument, String>, CustomMongoOfferRepository {
    fun findAllBySellerId(sellerId: String, pageable: Pageable): Page<OfferDocument>
}

interface CustomMongoOfferRepository {
    fun findWithFilters(
        searchingPhrases: List<String>,
        bookConditions: List<String>,
        offerType: List<String>,
        priceFrom: BigDecimal?,
        priceTo: BigDecimal?,
        location: String?,
        categoryId: String?,
        pageable: Pageable
    ): Page<OfferDocument>
}

class CustomMongoOfferRepositoryImpl(private val mongoTemplate: MongoTemplate) : CustomMongoOfferRepository {
    override fun findWithFilters(
        searchingPhrases: List<String>,
        bookConditions: List<String>,
        offerType: List<String>,
        priceFrom: BigDecimal?,
        priceTo: BigDecimal?,
        location: String?,
        categoryId: String?,
        pageable: Pageable
    ): Page<OfferDocument> {
        val query =
            buildQuery(searchingPhrases, bookConditions, offerType, priceFrom, priceTo, location, categoryId, pageable)

        val result = mongoTemplate.find(query, OfferDocument::class.java)

        return PageableExecutionUtils.getPage(result, pageable) {
            mongoTemplate.count(query.limit(-1).skip(-1), OfferDocument::class.java)
        }
    }

    private fun buildQuery(
        searchingPhrases: List<String>,
        bookConditions: List<String>,
        offerType: List<String>,
        priceFrom: BigDecimal?,
        priceTo: BigDecimal?,
        location: String?,
        categoryId: String?,
        pageable: Pageable
    ) = Query().apply {
        addCriteria(searchTextCriteria(searchingPhrases))
        addCriteria(Criteria.where("book.condition").`in`(bookConditions))
        addCriteria(Criteria.where("type").`in`(offerType))
        priceFrom?.let {
            if (priceTo == null) {
                addCriteria(Criteria.where("price").gte(it))
            } else {
                addCriteria(Criteria.where("price").gte(it).lte(priceTo))
            }
        }
        priceTo?.let {
            if (priceFrom == null) {
                addCriteria(Criteria.where("price").lte(it))
            }
        }
        location?.let {
            addCriteria(Criteria.where("location").`is`(it))
        }
        categoryId?.let {
            addCriteria(Criteria.where("category.id").`is`(it))
        }
    }

    private fun searchTextCriteria(searchingPhrases: List<String>) = Criteria().orOperator(
        Criteria.where("book.title")
            .regex(searchingPhrases.joinToString(separator = "|"), "i"),
        Criteria.where("book.isbn")
            .regex(searchingPhrases.joinToString(separator = "|"), "i"),
        Criteria.where("book.author")
            .regex(searchingPhrases.joinToString(separator = "|"), "i")
    )
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
