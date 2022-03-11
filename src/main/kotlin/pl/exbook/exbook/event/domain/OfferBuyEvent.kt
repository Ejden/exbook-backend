package pl.exbook.exbook.event.domain

import java.time.Instant
import pl.exbook.exbook.shared.EventId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

class OfferBuyEvent(
    eventId: EventId,
    timestamp: Instant,
    val offerId: OfferId,
    val sellerId: UserId,
    val buyerId: UserId,
    val quantity: Int
) : BaseEvent(eventId, timestamp)
