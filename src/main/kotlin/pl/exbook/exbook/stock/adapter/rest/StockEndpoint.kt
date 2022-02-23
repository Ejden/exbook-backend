package pl.exbook.exbook.stock.adapter.rest

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.StockId
import pl.exbook.exbook.stock.StockFacade
import pl.exbook.exbook.stock.domain.Stock

@RestController
@RequestMapping("api/stock")
class StockEndpoint(private val stockFacade: StockFacade) {
    @GetMapping("{stockId}", produces = [ContentType.V1])
    fun getStock(@PathVariable stockId: StockId): StockDto = stockFacade.getStock(stockId).toDto()

    @DeleteMapping("{stockId}", consumes = [ContentType.V1], produces = [ContentType.V1])
    fun getFromStock(
        @PathVariable stockId: StockId,
        request: GetFromStockRequest
    ): StockDto = stockFacade.getFromStock(stockId, request.amount).toDto()

    @PutMapping("{stockId}", consumes = [ContentType.V1], produces = [ContentType.V1])
    fun addToStock(
        @PathVariable stockId: StockId,
        request: AddToStockRequest
    ): StockDto = stockFacade.addToStock(stockId, request.amount).toDto()

    @PostMapping(consumes = [ContentType.V1], produces = [ContentType.V1])
    fun createStockForOffer(
        request: CreateStockRequest
    ): StockDto = stockFacade.createStockForOffer(OfferId(request.offerId), request.startQuantity).toDto()
}

data class GetFromStockRequest(
    val amount: Int
)

data class AddToStockRequest(
    val amount: Int
)

data class CreateStockRequest(
    val offerId: String,
    val startQuantity: Int
)

data class StockDto(
    val id: String,
    val offerId: String,
    val inStock: Int
)

private fun Stock.toDto() = StockDto(
    id = this.id.raw,
    offerId = this.offerId.raw,
    inStock = this.inStock
)
