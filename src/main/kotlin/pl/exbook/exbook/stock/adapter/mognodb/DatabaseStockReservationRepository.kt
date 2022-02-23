package pl.exbook.exbook.stock.adapter.mognodb

import org.springframework.stereotype.Component
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.StockReservationId
import pl.exbook.exbook.stock.domain.StockReservation
import pl.exbook.exbook.stock.domain.StockReservationRepository

@Component
class DatabaseStockReservationRepository(
    private val repository: MongoStockReservationRepository
) : StockReservationRepository {
    override fun getReservation(
        reservationId: StockReservationId
    ): StockReservation? = repository.getById(reservationId.raw)?.toDomain()

    override fun saveReservation(
        reservation: StockReservation
    ): StockReservation = repository.save(reservation.toDocument()).toDomain()

    override fun removeReservation(reservationId: StockReservationId) = repository.removeById(reservationId.raw)
}

private fun StockReservation.toDocument() = StockReservationDocument(
    id = this.reservationId.raw,
    stockId = this.stockId.raw,
    amount = this.amount
)

private fun StockReservationDocument.toDomain() = StockReservation(
    reservationId = StockReservationId(this.id),
    stockId = StockId(this.stockId),
    amount = this.amount
)
