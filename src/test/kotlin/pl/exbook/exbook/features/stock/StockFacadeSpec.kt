package pl.exbook.exbook.features.stock

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.StockDomainAbility
import pl.exbook.exbook.stock.domain.InsufficientStockException
import pl.exbook.exbook.stock.domain.NonPositiveAmountException

class StockFacadeSpec : ShouldSpec({
    val domain = StockDomainAbility()

    context("create stock for offer") {
        withData(0L, 1L, 2L, 3L) { startQuantity ->
            // when
            val result = domain.facade.createStock(startQuantity)

            val stock = domain.facade.getStock(result.id)

            // then
            stock.shouldNotBeNull()
            stock.inStock shouldBeExactly startQuantity
            stock.reserved shouldBeExactly 0L
        }
    }

    should("throw error when trying to create stock with negative inStock value") {
        // when
        shouldThrowExactly<NonPositiveAmountException> {
            domain.facade.createStock(-1)
        }
    }

    should("get stock by stock id") {
        // given
        val result = domain.facade.createStock(10)

        // when
        val stock = domain.facade.getStock(result.id)

        // then
        stock.shouldNotBeNull()
        stock.id shouldBe result.id
    }

    should("get from stock") {
        // given
        val stockId = domain.facade.createStock(100).id

        // when
        domain.facade.getFromStock(stockId, 5)

        val stock = domain.facade.getStock(stockId)

        // then
        stock.reserved shouldBeExactly 0
        stock.inStock shouldBeExactly 95
    }

    should("throw an error when trying to get more from stock than actual amount in stock") {
        // given
        val stockId = domain.facade.createStock(100).id

        // then
        shouldThrowExactly<InsufficientStockException> {
            domain.facade.getFromStock(stockId, 105)
        }
    }

    should("throw an error when trying to get invalid amount from stock") {
        // given
        val stockId = domain.facade.createStock(100).id

        // then
        shouldThrowExactly<NonPositiveAmountException> {
            domain.facade.getFromStock(stockId, -1)
        }
    }

    should("add to stock") {
        // given
        val stockId = domain.facade.createStock(100).id

        // when
        domain.facade.addToStock(stockId, 5)

        val stock = domain.facade.getStock(stockId)

        // then
        stock.reserved shouldBeExactly 0
        stock.inStock shouldBeExactly 105
    }

    should("throw an error when trying to add negative number to stock") {
        // given
        val stockId = domain.facade.createStock(100).id

        // then
        shouldThrowExactly<NonPositiveAmountException> {
            domain.facade.addToStock(stockId, -1)
        }
    }
})
