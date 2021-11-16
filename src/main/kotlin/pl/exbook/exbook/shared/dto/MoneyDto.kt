package pl.exbook.exbook.shared.dto

import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.Money
import java.math.BigDecimal

data class MoneyDto(
    val amount: BigDecimal,
    val currency: String
)

fun Money.toDto() = MoneyDto(
    amount = this.amount,
    currency = this.currency.name
)

fun MoneyDto.toDomain() = Money(
    amount = this.amount,
    currency = Currency.valueOf(this.currency)
)
