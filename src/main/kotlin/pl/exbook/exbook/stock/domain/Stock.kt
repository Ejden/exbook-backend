package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.StockId

data class Stock(
    val id: StockId,
    val inStock: Int,
    val reserved: Int
) {
    fun decreaseStock(amount: Int): Stock = this
        .validatePositiveNumber(amount)
        .validateDecreaseAmount(amount)
        .copy(inStock = this.inStock - amount)

    fun increaseStock(amount: Int): Stock = this
        .validatePositiveNumber(amount)
        .copy(inStock = this.inStock + amount)

    fun reserve(amount: Int): Stock = this
        .validatePositiveNumber(amount)
        .validateDecreaseAmount(amount)
        .copy(inStock = this.inStock - amount, reserved = this.reserved + amount)

    fun confirmReservation(reservation: StockReservation): Stock = this
        .validatePositiveNumber(reservation.amount)
        .validateReservationDecreaseAmount(reservation.amount)
        .copy(reserved = this.reserved - reservation.amount)

    fun cancelReservation(reservation: StockReservation): Stock = this
        .validatePositiveNumber(reservation.amount)
        .validateReservationDecreaseAmount(reservation.amount)
        .copy(reserved = this.reserved - reservation.amount, inStock = this.inStock + reservation.amount)

    private fun validatePositiveNumber(amount: Int): Stock {
        if (amount < 0) {
            throw NonPositiveAmountException(this.id, amount)
        }

        return this
    }

    private fun validateDecreaseAmount(amount: Int): Stock {
        if (this.inStock < amount) {
            throw InsufficientStockException(this.id, amount, this.inStock)
        }

        return this
    }

    private fun validateReservationDecreaseAmount(amount: Int): Stock {
        if (this.reserved < amount) {
            throw RuntimeException("Tried to decrease reserve by amount bigger than actual reserved")
        }

        return this
    }

    init {
        validatePositiveNumber(this.inStock)
    }
}
