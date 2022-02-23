package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockId

class NonPositiveAmountException(
    stockId: StockId,
    amount: Int
) : RuntimeException("Tried to modify stock ${stockId.raw} amount with negative amount: $amount")
