package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.shared.StockReservationId

class StockReservationNotFoundException(
    stockReservationId: StockReservationId
) : NotFoundException("Stock reservation with id = ${stockReservationId.raw} not found")
