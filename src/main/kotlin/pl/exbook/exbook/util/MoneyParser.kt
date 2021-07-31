package pl.exbook.exbook.util

import java.lang.Exception
import java.lang.RuntimeException

private val moneyToIntRegex = Regex("^([0-9]+)[,.]([0-9]{2})$")

fun parseMoneyToInt(value: String): Int {
    try {
        val matches = moneyToIntRegex.matchEntire(value)!!.groupValues
        val big = matches[1].toInt()
        val small = matches[2].toInt()
        return (big * 100) + small
    } catch (e: Exception) {
        throw MoneyToIntParseException(value)
    }
}

fun parseMoneyToString(value: Int): String {
    try {
        val big: Int = (value / 100)
        val small: Int = value - (big * 100)
        return "$big.${if (small < 10) "0" else ""}$small"
    } catch (e: Exception) {
        throw MoneyToStringParseException(value)
    }
}

class MoneyToIntParseException(providedValue: String): RuntimeException("Couldn't parse $providedValue to Int")
class MoneyToStringParseException(providedValue: Int): RuntimeException("Couldn't parse $providedValue to String")
