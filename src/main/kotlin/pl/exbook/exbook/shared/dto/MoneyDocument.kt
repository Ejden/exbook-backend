package pl.exbook.exbook.shared.dto

import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.Money
import java.math.BigDecimal

data class MoneyDocument(
    val amount: BigDecimal,
    val currency: String
) {
    fun toDomain() = Money(
        amount = this.amount,
        currency = Currency.valueOf(this.currency)
    )
}

fun Money.toDocument() = MoneyDocument(
    amount = this.amount,
    currency = this.currency.name
)
