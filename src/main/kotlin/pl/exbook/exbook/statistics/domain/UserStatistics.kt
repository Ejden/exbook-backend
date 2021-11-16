package pl.exbook.exbook.statistics.domain

import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.shared.UserStatisticsId
import java.math.BigDecimal
import java.time.Instant

class UserStatistics(
    val id: UserStatisticsId? = null,
    val userId: UserId,
    val timestamp: Instant,
    val soldOffers: Long,
    val exchangedOffers: Long,
    val boughtOffers: Long,
    val earnedFromSales: BigDecimal,
    val totalOfferViews: Long
)
