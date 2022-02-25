package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.StockId

interface StockRepository {
    fun getStock(stockId: StockId): Stock?

    fun getStockForOffer(offerId: OfferId): Stock?

    fun saveStock(stock: Stock): Stock
}
