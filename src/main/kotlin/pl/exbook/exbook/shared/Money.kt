package pl.exbook.exbook.shared

import java.math.BigDecimal

class Money(
    val amount: BigDecimal,
    val currency: Currency
) : Comparable<Money> {

    companion object {
        fun zero(currency: Currency): Money = Money(BigDecimal("0.00"), currency)

        fun zeroPln(): Money = zero(Currency.PLN)

        fun sum(money: List<Money?>): Money {
            return money.reduce { sum, x -> if (x != null) sum?.plus(x) else sum?.plus(zero(sum.currency)) } ?: zero(Currency.PLN)
        }
    }

    override fun compareTo(other: Money): Int {
        return amount.compareTo(other.amount)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Money) {
            return false
        }

        return this.amount == other.amount && this.currency == other.currency
    }

    operator fun plus(other: Money): Money {
        if (this.currency != other.currency) throw InconsistentCurrencyException()
        return Money(this.amount.plus(other.amount).setScale(2), this.currency)
    }

    operator fun minus(other: Money): Money {
        if (this.currency != other.currency) throw InconsistentCurrencyException()
        return Money(this.amount.minus(other.amount).setScale(2), this.currency)
    }

    operator fun times(value: Long): Money {
        return Money(this.amount.times(BigDecimal.valueOf(value)), this.currency)
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + currency.hashCode()
        return result
    }
}

class InconsistentCurrencyException : RuntimeException()
