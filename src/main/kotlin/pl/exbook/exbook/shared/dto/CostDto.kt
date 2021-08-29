package pl.exbook.exbook.shared.dto

import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.util.parseMoneyToString

data class CostDto(
    val amount: String,
    val currency: String
)

fun Money.toDto() = CostDto(
    amount = this.amount.toString(),
    currency = this.currency.name
)
