package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockId

class InsufficientStockException(
    stockId: StockId,
    requestedAmount: Int,
    availableAmount: Int
) : RuntimeException("Tried to reserve $requestedAmount for ${stockId.raw} stock amount but actual is $availableAmount")
