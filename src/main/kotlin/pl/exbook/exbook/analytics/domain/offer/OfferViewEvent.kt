package pl.exbook.exbook.analytics.domain.offer

import pl.exbook.exbook.shared.EventId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

data class OfferViewEvent(
    val eventId: EventId,
    val offerId: OfferId,
    val viewer: Viewer
) {
    data class Viewer(
        val userId: UserId?
    )
}
