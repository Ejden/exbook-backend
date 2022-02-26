package pl.exbook.exbook.stock

import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.StockReservationId
import pl.exbook.exbook.stock.domain.Stock
import pl.exbook.exbook.stock.domain.StockReservation
import pl.exbook.exbook.stock.domain.StockService

@Service
class StockFacade(private val stockService: StockService) {
    fun getStock(stockId: StockId): Stock = stockService.getStock(stockId)

    fun getStockForOffer(offerId: OfferId): Stock = stockService.getStockForOffer(offerId)

    fun getFromStock(stockId: StockId, amount: Int): Stock = stockService.getFromStock(stockId, amount)

    fun addToStock(stockId: StockId, amount: Int): Stock = stockService.addToStock(stockId, amount)

    fun reserve(stockId: StockId, amount: Int): StockReservation = stockService.reserve(stockId, amount)

    fun confirmReservation(reservationId: StockReservationId) = stockService.confirmReservation(reservationId)

    fun cancelReservation(reservationId: StockReservationId) = stockService.cancelReservation(reservationId)

    fun createStockForOffer(offerId: OfferId, startQuantity: Int): Stock = stockService.createStockForOffer(
        offerId,
        startQuantity
    )
}
