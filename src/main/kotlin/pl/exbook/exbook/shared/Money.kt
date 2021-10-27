package pl.exbook.exbook.shared

import java.math.BigDecimal

class Money(
    val amount: BigDecimal,
    val currency: Currency
) : Comparable<Money> {

    companion object {
        fun zero(currency: Currency): Money = Money(BigDecimal("0.00"), currency)

        fun sum(money: List<Money?>): Money {
            return money.reduce { sum, x -> if (x != null) sum?.plus(x) else sum?.plus(zero(sum.currency)) } ?: zero(Currency.PLN)
        }
    }

    override fun compareTo(other: Money): Int {
        return amount.compareTo(other.amount)
    }

    operator fun plus(other: Money): Money {
        if (this.currency != other.currency) throw InconsistentCurrencyException()
        return Money(this.amount.plus(other.amount).setScale(2), this.currency)
    }
}

class InconsistentCurrencyException : RuntimeException()
