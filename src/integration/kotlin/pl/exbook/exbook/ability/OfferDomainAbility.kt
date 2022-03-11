package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import pl.exbook.exbook.offer.adapter.rest.dto.CreateOfferRequest
import pl.exbook.exbook.offer.adapter.rest.dto.OfferDto
import pl.exbook.exbook.offer.adapter.rest.dto.UpdateOfferRequest
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.utils.createHttpEntity
import pl.exbook.exbook.utils.plnDto

class OfferDomainAbility(private val restTemplate: TestRestTemplate) {
    fun thereIsOffer(
        bookAuthor: String = "Jan",
        bookTitle: String = "Jan na drzewie",
        bookCondition: String = "NEW",
        isbn: String? = "1234567890",
        description: String = "Offer description",
        type: String = "EXCHANGE_AND_BUY",
        price: MoneyDto? = "10.00".plnDto(),
        location: String = "Warsaw",
        categoryId: String = sampleCategoryId.raw,
        shippingMethods: List<CreateOfferRequest.ShippingMethod> = listOf(
            CreateOfferRequest.ShippingMethod(
                id = sampleShippingMethodId.raw,
                price = "10.00".plnDto()
            )),
        initialStock: Int,
        token: String? = null
    ): ResponseEntity<OfferDto> {
        val requestBody = CreateOfferRequest(
            book = CreateOfferRequest.Book(
                author = bookAuthor,
                title = bookTitle,
                isbn = isbn,
                condition = bookCondition
            ),
            description = description,
            category = CreateOfferRequest.Category(categoryId),
            type = type,
            price = price,
            location = location,
            shippingMethods = shippingMethods,
            initialStock = initialStock
        )

        return restTemplate.postForEntity(
            "/api/offers",
            createHttpEntity(body = requestBody, withAcceptHeader = true, withContentTypeHeader = true, token = token),
            OfferDto::class.java
        )
    }

    fun createOffer(requestBody: CreateOfferRequest, token: String? = null): ResponseEntity<String> {
        return restTemplate.postForEntity(
            "/api/offers",
            createHttpEntity(body = requestBody, withAcceptHeader = true, withContentTypeHeader = true, token = token),
            String::class.java
        )
    }

    fun updateOffer(offerId: String, requestBody: UpdateOfferRequest, token: String? = null): ResponseEntity<String> {
        return restTemplate.exchange(
            "/api/offers/$offerId",
            HttpMethod.PUT,
            createHttpEntity(body = requestBody, withAcceptHeader = true, withContentTypeHeader = true, token = token),
            String::class.java
        )
    }

    fun getOffer(offerId: String): ResponseEntity<String> {
        return restTemplate.getForEntity("/api/offers/$offerId", String::class.java)
    }
}
