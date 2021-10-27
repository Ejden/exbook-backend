package pl.exbook.exbook.statistics.adapter.mongodb

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.Repository
import java.math.BigDecimal
import java.time.Instant

interface MongoUserStatisticsRepository : MongoRepository<UserStatisticsDocument, String> {

    fun findByUserId(userId: String, pageable: Pageable): Page<UserStatisticsDocument>

    fun save(userStatisticsDocument: UserStatisticsDocument): UserStatisticsDocument
}

@Document(collation = "user-statistics")
data class UserStatisticsDocument(
    @Id
    val id: String?,
    val userId: String,
    val timestamp: Instant,
    val soldOffers: Long,
    val exchangedOffers: Long,
    val boughtOffers: Long,
    val earnedFromSales: BigDecimal,
    val totalOfferViews: Long
)
