package pl.exbook.exbook.statistics

import pl.exbook.exbook.event.events.OfferViewEvent
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.statistics.domain.UserStatistics
import pl.exbook.exbook.statistics.domain.UserStatisticsRepository
import java.math.BigDecimal
import java.time.Instant

class UserStatisticsFacade(private val userStatisticsRepository: UserStatisticsRepository) {

    fun updateStatistics(offerViewEvent: OfferViewEvent) {
        userStatisticsRepository.save(UserStatistics(
            userId = UserId("33"),
            timestamp = Instant.now(),
            soldOffers = 0,
            exchangedOffers = 0,
            boughtOffers = 0,
            earnedFromSales = BigDecimal("0.00"),
            totalOfferViews = 0
        ))
    }
}
