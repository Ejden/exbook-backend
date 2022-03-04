package pl.exbook.exbook.features

import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import pl.exbook.exbook.BaseIntegrationSpec
import pl.exbook.exbook.ability.ShippingMethodDomainAbility
import pl.exbook.exbook.shared.TestData.sampleShippingMethodName
import pl.exbook.exbook.shippingmethod.adapter.rest.dto.CreateShippingMethodRequest
import pl.exbook.exbook.utils.plnDto
import java.math.BigDecimal

class ShippingMethodSpec(private val rest: TestRestTemplate) : BaseIntegrationSpec({
    val domain = ShippingMethodDomainAbility(rest)

    should("add shipping method") {
        // given
        val requestBody = CreateShippingMethodRequest(
            name = sampleShippingMethodName,
            pickupPointMethod = true,
            cost = CreateShippingMethodRequest.Cost(
                defaultCost = "10.00".plnDto(),
                canBeOverridden = true
            )
        )

        // when
        val response = domain.createShippingMethod(requestBody)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson """
            {
                "name": "$sampleShippingMethodName",
                "pickupPointMethod": true,
                "defaultCost": {
                    "cost": {
                        "amount": 10.00,
                        "currency": "PLN"
                    },
                    "canBeOverridden": true
                }
            }
        """
        response.body!!.shouldContainJsonKey("id")
    }

    should("return status 422 when request body validation failed") {
        // given
        val requestBody = CreateShippingMethodRequest(
            name = sampleShippingMethodName,
            pickupPointMethod = true,
            cost = CreateShippingMethodRequest.Cost(
                defaultCost = "-10.00".plnDto(),
                canBeOverridden = true
            )
        )

        // when
        val response = domain.createShippingMethod(requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("return status 422 when failed to add shipping method due to validation") {
        // given
        domain.thereIsShippingMethod(name = sampleShippingMethodName)
        val requestBody = CreateShippingMethodRequest(
            name = sampleShippingMethodName,
            pickupPointMethod = true,
            cost = CreateShippingMethodRequest.Cost(
                defaultCost = "-10.00".plnDto(),
                canBeOverridden = true
            )
        )

        // when
        val response = domain.createShippingMethod(requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("get all shipping methods") {
        // given
        val shipping1 = domain.thereIsShippingMethod(
            name = sampleShippingMethodName,
            pickupMethod = true,
            defaultCost = BigDecimal("11.50"),
            costCurrency = "PLN",
            costCanBeOverridden = false
        )
        val shipping2 = domain.thereIsShippingMethod(
            name = "other-shipping-method",
            pickupMethod = false,
            defaultCost = BigDecimal("11.00"),
            costCurrency = "PLN",
            costCanBeOverridden = true
        )

        // when
        val response = domain.getAllShippingMethods()

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualJson """
            {
                "shippingMethods": [
                    {
                        "id": "${shipping1.body!!.id}",
                        "name": "$sampleShippingMethodName",
                        "pickupPointMethod": true,
                        "defaultCost": {
                            "cost": {
                                "amount": 11.50,
                                "currency": "PLN"
                            },
                            "canBeOverridden": false
                        }
                    },
                    {
                        "id": "${shipping2.body!!.id}",
                        "name": "other-shipping-method",
                        "pickupPointMethod": false,
                        "defaultCost": {
                            "cost": {
                                "amount": 11.00,
                                "currency": "PLN"
                            },
                            "canBeOverridden": true
                        }
                    }
                ]
            }
        """
    }
})
