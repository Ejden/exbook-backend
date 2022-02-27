package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.ValidationException

class InsufficientStockException(
    stockId: StockId,
    requestedAmount: Int,
    availableAmount: Int
) : ValidationException("Tried to reserve $requestedAmount for ${stockId.raw} stock amount but actual is $availableAmount")
