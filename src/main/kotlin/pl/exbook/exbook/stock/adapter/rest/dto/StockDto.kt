package pl.exbook.exbook.stock.adapter.rest.dto

import pl.exbook.exbook.stock.domain.Stock

data class StockDto(
    val id: String,
    val offerId: String,
    val inStock: Int
) {
    companion object {
        fun fromDomain(stock: Stock) = StockDto(
            id = stock.id.raw,
            offerId = stock.offerId.raw,
            inStock = stock.inStock
        )
    }
}
