package pl.exbook.exbook.analytics.domain.offer

import mu.KLogging
import org.springframework.stereotype.Service

@Service
class OfferEventsFacade {
    fun handleEvent(event: OfferViewEvent) {
        logger.info { "OFFER VIEW EVENT: $event" }
    }

    companion object : KLogging()
}
