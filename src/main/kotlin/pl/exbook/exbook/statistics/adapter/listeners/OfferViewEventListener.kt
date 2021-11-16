package pl.exbook.exbook.statistics.adapter.listeners

import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import pl.exbook.exbook.event.events.OfferViewEvent
import pl.exbook.exbook.statistics.UserStatisticsFacade

@Component
class OfferViewEventListener(private val userStatisticsFacade: UserStatisticsFacade) : ApplicationListener<OfferViewEvent> {

    override fun onApplicationEvent(event: OfferViewEvent) {
        userStatisticsFacade.updateStatistics(event)
    }
}