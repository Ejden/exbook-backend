package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.shared.TestData.sampleShippingMethodName
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shippingmethod.adapter.rest.dto.CreateShippingMethodRequest
import pl.exbook.exbook.shippingmethod.adapter.rest.dto.ShippingMethodDto
import pl.exbook.exbook.utils.createHttpEntity
import java.math.BigDecimal

class ShippingMethodDomainAbility(private val restTemplate: TestRestTemplate) {
    fun thereIsShippingMethod(
        name: String = sampleShippingMethodName,
        pickupMethod: Boolean = false,
        defaultCost: BigDecimal = BigDecimal("10.00"),
        costCurrency: String = "PLN",
        costCanBeOverridden: Boolean = true
    ): ResponseEntity<ShippingMethodDto> {
        val requestBody = CreateShippingMethodRequest(
            name = name,
            pickupPointMethod = pickupMethod,
            cost = CreateShippingMethodRequest.Cost(
                defaultCost = MoneyDto(defaultCost, costCurrency),
                canBeOverridden = costCanBeOverridden
            )
        )

        return restTemplate.postForEntity(
            "/api/shipping",
            createHttpEntity(requestBody, withAcceptHeader = true, withContentTypeHeader = true),
            ShippingMethodDto::class.java
        )
    }

    fun createShippingMethod(requestBody: CreateShippingMethodRequest): ResponseEntity<String> {
        return restTemplate.postForEntity(
            "/api/shipping",
            createHttpEntity(requestBody, withAcceptHeader = true, withContentTypeHeader = true),
            String::class.java
        )
    }

    fun getAllShippingMethods(): ResponseEntity<String> {
        return restTemplate.getForEntity("/api/shipping", String::class.java)
    }
}
