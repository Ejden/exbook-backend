package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.baskettransaction.adapter.rest.dto.PreviewPurchaseRequest
import pl.exbook.exbook.utils.createHttpEntity

class BasketTransactionDomainAbility(private val restTemplate: TestRestTemplate) {
    fun previewPurchase(requestBody: PreviewPurchaseRequest, token: String? = null): ResponseEntity<String> {
        return restTemplate.exchange(
            "/api/purchase/preview",
            HttpMethod.PUT,
            createHttpEntity(requestBody, withAcceptHeader = true, withContentTypeHeader = true, token = token),
            String::class.java
        )
    }
}
