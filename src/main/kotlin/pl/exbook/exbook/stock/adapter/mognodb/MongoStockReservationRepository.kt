package pl.exbook.exbook.stock.adapter.mognodb

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.Repository

@org.springframework.stereotype.Repository
interface MongoStockReservationRepository : Repository<StockReservationDocument, String> {
    fun getById(id: String): StockReservationDocument?

    fun save(stockReservation: StockReservationDocument): StockReservationDocument

    fun removeById(id: String)
}

@Document(collection = "stockReservation")
data class StockReservationDocument(
    val id: String,
    val stockId: String,
    val amount: Long
)
