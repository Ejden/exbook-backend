package pl.exbook.exbook.stock.domain

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.UUID
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.shared.StockReservationId

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val stockReservationRepository: StockReservationRepository
) {
    private val semaphores: Cache<StockId, Semaphore> = CacheBuilder.newBuilder()
        .maximumSize(100000)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build()

    fun getStock(stockId: StockId): Stock = stockRepository.getStock(stockId) ?: throw StockNotFoundException(stockId)

    fun getReservation(
        reservationId: StockReservationId
    ): StockReservation = stockReservationRepository.getReservation(reservationId)
        ?: throw StockReservationNotFoundException(reservationId)

    fun reserve(stockId: StockId, amount: Int): StockReservation {
        val semaphore = lock(stockId)

        try {
            val stock = getStock(stockId)
            val modifiedStock = stock.reserve(amount)
            stockRepository.saveStock(modifiedStock)
        } finally {
            semaphore.release()
        }

        val reservation = createReservation(stockId, amount)
        return stockReservationRepository.saveReservation(reservation)
    }

    fun confirmReservation(reservationId: StockReservationId) {
        val stockReservation = getReservation(reservationId)
        val semaphore = lock(stockReservation.stockId)

        try {
            val stock = getStock(stockReservation.stockId)
            val updatedStock = stock.confirmReservation(stockReservation)
            stockRepository.saveStock(updatedStock)
            stockReservationRepository.removeReservation(reservationId)
        } finally {
            semaphore.release()
        }
    }

    fun cancelReservation(reservationId: StockReservationId) {
        val stockReservation = getReservation(reservationId)
        val semaphore = lock(stockReservation.stockId)

        try {
            val stock = getStock(stockReservation.stockId)
            val updatedStock = stock.cancelReservation(stockReservation)

            stockRepository.saveStock(updatedStock)
            stockReservationRepository.removeReservation(reservationId)
        } finally {
            semaphore.release()
        }
    }

    fun getFromStock(stockId: StockId, amount: Int): Stock {
        val semaphore = lock(stockId)

        try {
            return getStock(stockId)
                .decreaseStock(amount)
                .let { stockRepository.saveStock(it) }
        } finally {
            semaphore.release()
        }
    }

    fun addToStock(stockId: StockId, amount: Int): Stock {
        val semaphore = lock(stockId)

        try {
            return getStock(stockId)
                .increaseStock(amount)
                .let { stockRepository.saveStock(it) }
        } finally {
            semaphore.release()
        }
    }

    fun createStockForOffer(offerId: OfferId, startQuantity: Int): Stock = Stock(
            id = StockId(UUID.randomUUID().toString()),
            offerId = offerId,
            inStock = startQuantity,
            reserved = 0
        ).let { stockRepository.saveStock(it) }

    private fun createReservation(stockId: StockId, amount: Int): StockReservation = StockReservation(
        reservationId = StockReservationId(UUID.randomUUID().toString()),
        stockId = stockId,
        amount = amount
    )

    private fun lock(stockId: StockId): Semaphore {
        val semaphore = semaphores.get(stockId) { Semaphore(1) }
        semaphore.acquire()
        return semaphore
    }
}
