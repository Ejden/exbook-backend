package pl.exbook.exbook.ability

import pl.exbook.exbook.adapters.InMemoryStockRepository
import pl.exbook.exbook.adapters.InMemoryStockReservationsRepository
import pl.exbook.exbook.stock.StockFacade
import pl.exbook.exbook.stock.domain.StockService
import pl.exbook.exbook.stock.domain.StockValidator

class StockDomainAbility {
    private val stockRepository: InMemoryStockRepository = InMemoryStockRepository()
    private val stockReservationRepository: InMemoryStockReservationsRepository = InMemoryStockReservationsRepository()
    private val stockValidator: StockValidator = StockValidator()
    private val stockService: StockService = StockService(
        stockRepository = stockRepository,
        stockReservationRepository = stockReservationRepository,
        stockValidator = stockValidator
    )
    val facade: StockFacade = StockFacade(stockService)
}
