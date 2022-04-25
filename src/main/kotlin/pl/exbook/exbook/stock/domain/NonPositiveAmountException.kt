package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.StockId

class NonPositiveAmountException(
    stockId: StockId,
    amount: Long
) : IllegalParameterException("Tried to modify stock ${stockId.raw} amount with negative amount: $amount")
