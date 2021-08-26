package pl.exbook.exbook.shared.dto

import pl.exbook.exbook.shared.Cost
import pl.exbook.exbook.util.parseMoneyToString

data class CostDto(
    val value: String,
    val currency: String
)

fun Cost.toDto() = CostDto(
    value = parseMoneyToString(this.value),
    currency = this.currency.name
)
