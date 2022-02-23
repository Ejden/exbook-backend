package pl.exbook.exbook.analytics.adapter.rest

import java.util.UUID
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.analytics.domain.offer.OfferEventsFacade
import pl.exbook.exbook.analytics.domain.offer.OfferViewEvent
import pl.exbook.exbook.shared.EventId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

@RestController
@RequestMapping("api/analytics")
class OfferAnalyticsEndpoint(private val offerEventsFacade: OfferEventsFacade) {
    @PostMapping("/offer-view")
    fun consumeEvent(@RequestBody event: OfferViewEventRequest) = offerEventsFacade.handleEvent(event.toDomain())
}

data class OfferViewEventRequest(
    val offerId: String,
    val viewer: Viewer
) {
    data class Viewer(
        val userId: String?
    )
}

private fun OfferViewEventRequest.toDomain() = OfferViewEvent(
    eventId = EventId(UUID.randomUUID().toString()),
    offerId = OfferId(this.offerId),
    viewer =  OfferViewEvent.Viewer(
        userId = this.viewer.userId?.let { UserId(it) }
    )
)
