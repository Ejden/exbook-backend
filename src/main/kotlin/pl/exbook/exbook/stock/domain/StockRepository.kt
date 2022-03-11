package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockId

interface StockRepository {
    fun getStock(stockId: StockId): Stock?

    fun saveStock(stock: Stock): Stock
}
