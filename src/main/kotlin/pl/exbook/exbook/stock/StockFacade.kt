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

    fun getFromStock(stockId: StockId, amount: Long): Stock = stockService.getFromStock(stockId, amount)

    fun addToStock(stockId: StockId, amount: Long): Stock = stockService.addToStock(stockId, amount)

    fun reserve(stockId: StockId, amount: Long): StockReservation = stockService.reserve(stockId, amount)

    fun confirmReservation(reservationId: StockReservationId) = stockService.confirmReservation(reservationId)

    fun cancelReservation(reservationId: StockReservationId) = stockService.cancelReservation(reservationId)

    fun createStock(startQuantity: Long): Stock = stockService.createStockForOffer(startQuantity)
}
