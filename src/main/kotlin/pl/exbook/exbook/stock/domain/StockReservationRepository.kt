package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockReservationId

interface StockReservationRepository {
    fun getReservation(reservationId: StockReservationId): StockReservation?

    fun saveReservation(reservation: StockReservation): StockReservation

    fun removeReservation(reservationId: StockReservationId)
}
