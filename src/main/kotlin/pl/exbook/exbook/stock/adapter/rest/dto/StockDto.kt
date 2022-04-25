package pl.exbook.exbook.stock.adapter.rest.dto

import pl.exbook.exbook.stock.domain.Stock

data class StockDto(
    val id: String,
    val inStock: Long
) {
    companion object {
        fun fromDomain(stock: Stock) = StockDto(
            id = stock.id.raw,
            inStock = stock.inStock
        )
    }
}
