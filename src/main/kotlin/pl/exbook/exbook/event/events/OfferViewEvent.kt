package pl.exbook.exbook.event.events

import org.springframework.context.ApplicationEvent
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

class OfferViewEvent(
    source: Any,
    val offerId: OfferId,
    val sellerId: UserId,
    val viewerId: UserId?
) : ApplicationEvent(source)
