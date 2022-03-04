package pl.exbook.exbook.features

import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import pl.exbook.exbook.BaseIntegrationSpec
import pl.exbook.exbook.ability.StockDomainAbility
import pl.exbook.exbook.shared.TestData.sampleStockId
import pl.exbook.exbook.stock.adapter.rest.dto.AddToStockRequest
import pl.exbook.exbook.stock.adapter.rest.dto.CreateStockRequest
import pl.exbook.exbook.stock.adapter.rest.dto.GetFromStockRequest

class StockEndpointSpec(private val rest: TestRestTemplate) : BaseIntegrationSpec({
    val domain = StockDomainAbility(rest)

    should("get stock") {
        // given
        val stockId = domain.thereIsStock(startQuantity = 10).body!!.id

        // when
        val response = domain.getStock(stockId)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualJson """
            {
                "id": "$stockId",
                "inStock": 10
            }
        """
    }

    should("return status 404 when trying to get non existing stock") {
        // when
        val response = domain.getStock(sampleStockId.raw)

        // then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    should("get from stock") {
        // given
        val stockId = domain.thereIsStock(startQuantity = 10).body!!.id
        val requestBody = GetFromStockRequest(amount = 5)

        // when
        val response = domain.getFromStock(stockId, requestBody)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualJson """
            {
                "id": "$stockId",
                "inStock": 5
            }
        """
    }

    should("return status 404 when trying to get from non existing stock") {
        // given
        val requestBody = GetFromStockRequest(amount = 5)

        // when
        val response = domain.getFromStock(sampleStockId.raw, requestBody)

        // then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    should("return status 422 when provided invalid request when trying to get from stock") {
        // given
        val stockId = domain.thereIsStock(startQuantity = 10).body!!.id
        val requestBody = GetFromStockRequest(amount = -3)

        // when
        val response = domain.getFromStock(stockId, requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("return status 422 when validation exception occurred") {
        // given
        val stockId = domain.thereIsStock(startQuantity = 10).body!!.id
        val requestBody = GetFromStockRequest(amount = 20)

        // when
        val response = domain.getFromStock(stockId, requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("add to stock") {
        // given
        val stockId = domain.thereIsStock(startQuantity = 10).body!!.id
        val requestBody = AddToStockRequest(amount = 20)

        // when
        val response = domain.addToStock(stockId, requestBody)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualJson """
            {
                "id": "$stockId",
                "inStock": 30
            }
        """
    }

    should("return status 404 when trying to add to non existing stock") {
        // given
        val requestBody = AddToStockRequest(amount = 20)

        // when
        val response = domain.addToStock(sampleStockId.raw, requestBody)

        // then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    should("return status 422 when provided invalid request trying to add to stock") {
        // given
        val stockId = domain.thereIsStock(startQuantity = 10).body!!.id
        val requestBody = AddToStockRequest(amount = -4)

        // when
        val response = domain.addToStock(stockId, requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("create stock") {
        // given
        val requestBody = CreateStockRequest(startQuantity = 10)

        // when
        val response = domain.createStock(requestBody)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!!.shouldContainJsonKeyValue("inStock", 10)
    }

    should("return status 422 when provided wrong start quantity while creating stock") {
        // given
        val requestBody = CreateStockRequest(startQuantity = -10)

        // when
        val response = domain.createStock(requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }
})
