package pl.exbook.exbook.stock.adapter.mognodb

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.Repository

@org.springframework.stereotype.Repository
interface MongoStockRepository : Repository<StockDocument, String> {
    fun findById(id: String): StockDocument?

    fun save(stock: StockDocument): StockDocument
}

@Document(collection = "stock")
data class StockDocument(
    @Id
    val id: String,
    val offerId: String,
    val inStock: Int,
    val reserved: Int
)
