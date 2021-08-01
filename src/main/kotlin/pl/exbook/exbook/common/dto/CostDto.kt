package pl.exbook.exbook.common.dto

import pl.exbook.exbook.common.Cost
import pl.exbook.exbook.util.parseMoneyToString

data class CostDto(
    val value: String,
    val currency: String
)

fun Cost.toDto() = CostDto(
    value = parseMoneyToString(this.value),
    currency = this.currency.name
)
