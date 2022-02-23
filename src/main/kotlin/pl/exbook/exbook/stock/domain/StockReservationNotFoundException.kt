package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockReservationId

class StockReservationNotFoundException(
    stockReservationId: StockReservationId
) : RuntimeException("Stock reservation with id = ${stockReservationId.raw} not found")
