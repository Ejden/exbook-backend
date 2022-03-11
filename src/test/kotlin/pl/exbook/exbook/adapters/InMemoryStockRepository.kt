package pl.exbook.exbook.adapters

import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.stock.domain.Stock
import pl.exbook.exbook.stock.domain.StockRepository

class InMemoryStockRepository : StockRepository {
    private val memory = mutableMapOf<StockId, Stock>()

    override fun getStock(stockId: StockId): Stock? = memory[stockId]

    override fun saveStock(stock: Stock): Stock {
        memory[stock.id] = stock
        return memory[stock.id]!!
    }
}
