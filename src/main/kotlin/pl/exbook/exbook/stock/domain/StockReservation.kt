package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.StockReservationId

data class StockReservation(
    val reservationId: StockReservationId,
    val stockId: StockId,
    val amount: Int
)
