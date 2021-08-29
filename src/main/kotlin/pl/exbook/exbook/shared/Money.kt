package pl.exbook.exbook.shared

import java.math.BigDecimal

data class Money(
    val amount: BigDecimal,
    val currency: Currency
)
