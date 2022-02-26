package pl.exbook.exbook

import java.math.BigDecimal
import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.Money

internal fun String.pln(): Money = Money(BigDecimal(this), Currency.PLN)
