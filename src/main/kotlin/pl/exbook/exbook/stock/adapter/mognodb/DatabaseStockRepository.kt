package pl.exbook.exbook.stock.adapter.mognodb

import org.springframework.stereotype.Component
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.stock.domain.Stock
import pl.exbook.exbook.stock.domain.StockRepository

@Component
class DatabaseStockRepository(private val mongoStockRepository: MongoStockRepository) : StockRepository {
    override fun getStock(stockId: StockId): Stock? = mongoStockRepository.findById(stockId.raw)
        ?.toDomain()

    override fun saveStock(stock: Stock): Stock = mongoStockRepository.save(stock.toDocument()).toDomain()

    override fun getStockForOffer(offerId: OfferId): Stock? = mongoStockRepository.findByOfferId(offerId.raw)?.toDomain()
}

private fun Stock.toDocument() = StockDocument(
    id = this.id.raw,
    offerId = this.offerId.raw,
    inStock = this.inStock,
    reserved = this.reserved
)

private fun StockDocument.toDomain() = Stock(
    id = StockId(this.id),
    offerId = OfferId(this.offerId),
    inStock = this.inStock,
    reserved = this.reserved
)
