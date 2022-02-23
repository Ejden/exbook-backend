package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockId

class StockNotFoundException(stockId: StockId) : RuntimeException("Stock ${stockId.raw} not found")
