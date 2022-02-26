package pl.exbook.exbook.features.stock

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.StockDomainAbility
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleStockReservationId
import pl.exbook.exbook.stock.domain.InsufficientStockException
import pl.exbook.exbook.stock.domain.StockReservationNotFoundException

class StockReservationSpec : ShouldSpec({
    val domain = StockDomainAbility()

    should("reserve stock") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id

        // when
        val result = domain.facade.reserve(stockId, 10)
        val stock = domain.facade.getStock(stockId)

        // then
        result.shouldNotBeNull()
        result.stockId shouldBe stockId
        result.amount shouldBeExactly 10

        // and
        stock.id shouldBe stockId
        stock.reserved shouldBeExactly 10
        stock.inStock shouldBeExactly 90
    }

    should("make couple reservation on stock") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id

        // when
        val result1 = domain.facade.reserve(stockId, 10)
        val result2 = domain.facade.reserve(stockId, 20)
        val stock = domain.facade.getStock(stockId)

        // then
        result1.stockId shouldBe stockId
        result1.amount shouldBeExactly 10

        // and
        result2.stockId shouldBe stockId
        result2.amount shouldBeExactly 20

        // and
        stock.id shouldBe stockId
        stock.reserved shouldBeExactly 30
        stock.inStock shouldBeExactly 70
    }

    should("throw an error while trying to reserve more stock than actual amount in stock") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id

        // then
        shouldThrowExactly<InsufficientStockException> {
            domain.facade.reserve(stockId, 101)
        }

        // and: stock wasn't changed
        val stock = domain.facade.getStock(stockId)
        stock.inStock shouldBeExactly 100
        stock.reserved shouldBeExactly 0
    }

    should("confirm reservation") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id
        val reservation = domain.facade.reserve(stockId, 5)

        // when
        domain.facade.confirmReservation(reservation.reservationId)
        val stock = domain.facade.getStock(stockId)

        // then
        stock.offerId shouldBe sampleOfferId
        stock.reserved shouldBeExactly 0
        stock.inStock shouldBeExactly 95
    }

    should("confirm couple reservation for one stock") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id
        val reservation1 = domain.facade.reserve(stockId, 5)
        val reservation2 = domain.facade.reserve(stockId, 10)

        // when
        domain.facade.confirmReservation(reservation1.reservationId)
        domain.facade.confirmReservation(reservation2.reservationId)
        val stock = domain.facade.getStock(stockId)

        // then
        stock.offerId shouldBe sampleOfferId
        stock.reserved shouldBeExactly 0
        stock.inStock shouldBeExactly 85
    }

    should("throw an error when trying to confirm non existing reservation") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id

        // then
        shouldThrowExactly<StockReservationNotFoundException> {
            domain.facade.confirmReservation(sampleStockReservationId)
        }
    }

    should("throw an error when trying to confirm reservation more than once") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id
        val reservation = domain.facade.reserve(stockId, 5)
        domain.facade.confirmReservation(reservation.reservationId)

        // then
        shouldThrowExactly<StockReservationNotFoundException> {
            domain.facade.confirmReservation(reservation.reservationId)
        }

        // and: stock was not modified
        val stock = domain.facade.getStock(stockId)

        stock.offerId shouldBe sampleOfferId
        stock.reserved shouldBeExactly 0
        stock.inStock shouldBeExactly 95
    }

    should("cancel stock reservation") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id
        val reservation = domain.facade.reserve(stockId, 5)

        // when
        domain.facade.cancelReservation(reservation.reservationId)

        val stock = domain.facade.getStock(stockId)

        // then
        stock.inStock shouldBeExactly 100
        stock.reserved shouldBeExactly 0
    }

    should("cancel more than one stock reservation") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id
        val reservation1 = domain.facade.reserve(stockId, 5)
        val reservation2 = domain.facade.reserve(stockId, 10)

        // when
        domain.facade.cancelReservation(reservation1.reservationId)
        domain.facade.cancelReservation(reservation2.reservationId)

        val stock = domain.facade.getStock(stockId)

        // then
        stock.inStock shouldBeExactly 100
        stock.reserved shouldBeExactly 0
    }

    should("throw an error when trying to cancel non existing stock reservation") {
        // given
        val stockId = domain.facade.createStockForOffer(sampleOfferId, 100).id

        // then
        shouldThrowExactly<StockReservationNotFoundException> {
            domain.facade.cancelReservation(sampleStockReservationId)
        }
    }
})
