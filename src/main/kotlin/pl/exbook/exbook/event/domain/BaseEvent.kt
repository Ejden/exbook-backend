package pl.exbook.exbook.event.domain

import java.time.Instant
import pl.exbook.exbook.shared.EventId

abstract class BaseEvent(
    val eventId: EventId,
    val timestamp: Instant
)
