package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.basket.adapter.rest.dto.AddItemToBasketRequest
import pl.exbook.exbook.basket.adapter.rest.dto.BasketDto
import pl.exbook.exbook.order.domain.Order.OrderType
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.utils.createHttpEntity

class BasketDomainAbility(private val restTemplate: TestRestTemplate) {
    fun thereIsItemInBasket(
        offerId: String = sampleOfferId.raw,
        orderType: String = OrderType.BUY.name,
        quantity: Long = 1,
        token: String? = null
    ): ResponseEntity<BasketDto> {
        val requestBody = AddItemToBasketRequest(offerId, orderType, quantity)
        return restTemplate.exchange(
            "/api/basket",
            HttpMethod.PUT,
            createHttpEntity(requestBody, withAcceptHeader = true, withContentTypeHeader = true, token = token),
            BasketDto::class.java
        )
    }
}
