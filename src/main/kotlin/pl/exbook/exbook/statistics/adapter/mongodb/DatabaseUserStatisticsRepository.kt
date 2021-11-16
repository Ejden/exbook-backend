package pl.exbook.exbook.statistics.adapter.mongodb

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.UserStatisticsId
import pl.exbook.exbook.statistics.domain.UserStatistics
import pl.exbook.exbook.statistics.domain.UserStatisticsRepository

class DatabaseUserStatisticsRepository(
    private val mongoUserStatisticsRepository: MongoUserStatisticsRepository
) : UserStatisticsRepository {

    override fun save(userStatistics: UserStatistics): UserStatistics {
        return mongoUserStatisticsRepository.save(userStatistics.toDocument()).toDomain()
    }

    fun findNewest(userId: UserId): UserStatistics? {
        return mongoUserStatisticsRepository
            .findByUserId(userId.raw, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "timestamp")))
            .content[0]?.toDomain()
    }
}

private fun UserStatistics.toDocument() = UserStatisticsDocument(
    id = this.id?.raw,
    userId = this.userId.raw,
    timestamp = this.timestamp,
    soldOffers = this.soldOffers,
    exchangedOffers = this.exchangedOffers,
    boughtOffers = this.boughtOffers,
    earnedFromSales = this.earnedFromSales,
    totalOfferViews = this.totalOfferViews
)

private fun UserStatisticsDocument.toDomain() = UserStatistics(
    id = UserStatisticsId(this.id!!),
    userId = UserId(this.userId),
    timestamp = this.timestamp,
    soldOffers = this.soldOffers,
    exchangedOffers = this.exchangedOffers,
    boughtOffers = this.boughtOffers,
    earnedFromSales = this.earnedFromSales,
    totalOfferViews = this.totalOfferViews
)
