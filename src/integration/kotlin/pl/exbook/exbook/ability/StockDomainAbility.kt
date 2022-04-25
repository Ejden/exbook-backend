package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.stock.adapter.rest.dto.AddToStockRequest
import pl.exbook.exbook.stock.adapter.rest.dto.CreateStockRequest
import pl.exbook.exbook.stock.adapter.rest.dto.GetFromStockRequest
import pl.exbook.exbook.stock.adapter.rest.dto.StockDto
import pl.exbook.exbook.utils.createHttpEntity

class StockDomainAbility(private val restTemplate: TestRestTemplate) {
    fun thereIsStock(startQuantity: Long = 100): ResponseEntity<StockDto> {
        val requestBody = CreateStockRequest(startQuantity)
        return restTemplate.postForEntity(
            "/api/stock",
            createHttpEntity(body = requestBody, withAcceptHeader = true, withContentTypeHeader = true),
            StockDto::class.java
        )
    }

    fun createStock(requestBody: CreateStockRequest): ResponseEntity<String> {
        return restTemplate.postForEntity(
            "/api/stock",
            createHttpEntity(body = requestBody, withAcceptHeader = true, withContentTypeHeader = true),
            String::class.java
        )
    }

    fun getStock(stockId: String): ResponseEntity<String> {
        return restTemplate.getForEntity("/api/stock/$stockId", String::class.java)
    }

    fun getFromStock(stockId: String, requestBody: GetFromStockRequest): ResponseEntity<String> {
        return restTemplate.exchange(
            "/api/stock/$stockId",
            HttpMethod.DELETE,
            createHttpEntity(body = requestBody, withAcceptHeader = true, withContentTypeHeader = true),
            String::class.java
        )
    }

    fun addToStock(stockId: String, requestBody: AddToStockRequest): ResponseEntity<String> {
        return restTemplate.exchange(
            "/api/stock/$stockId",
            HttpMethod.PUT,
            createHttpEntity(body = requestBody, withAcceptHeader = true, withContentTypeHeader = true),
            String::class.java
        )
    }
}
