package pl.exbook.exbook.adapters

import pl.exbook.exbook.shared.StockReservationId
import pl.exbook.exbook.stock.domain.StockReservation
import pl.exbook.exbook.stock.domain.StockReservationRepository

class InMemoryStockReservationsRepository : StockReservationRepository {
    private val memory = mutableMapOf<StockReservationId, StockReservation>()

    override fun getReservation(reservationId: StockReservationId): StockReservation? = memory[reservationId]

    override fun saveReservation(reservation: StockReservation): StockReservation {
        memory[reservation.reservationId] = reservation
        return memory[reservation.reservationId]!!
    }

    override fun removeReservation(reservationId: StockReservationId) {
        memory.remove(reservationId)
    }
}
