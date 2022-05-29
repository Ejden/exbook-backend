package pl.exbook.exbook.features

import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import pl.exbook.exbook.BaseIntegrationSpec
import pl.exbook.exbook.ability.AuthAbility
import pl.exbook.exbook.ability.CategoryDomainAbility
import pl.exbook.exbook.ability.OfferDomainAbility
import pl.exbook.exbook.ability.ShippingMethodDomainAbility
import pl.exbook.exbook.offer.adapter.rest.dto.CreateOfferRequest
import pl.exbook.exbook.offer.adapter.rest.dto.UpdateOfferRequest
import pl.exbook.exbook.shared.TestData.otherSampleSellerEmail
import pl.exbook.exbook.shared.TestData.otherSampleSellerUsername
import pl.exbook.exbook.shared.TestData.sampleAdminEmail
import pl.exbook.exbook.shared.TestData.sampleAdminUsername
import pl.exbook.exbook.shared.TestData.sampleCategoryId
import pl.exbook.exbook.shared.TestData.sampleOfferId
import pl.exbook.exbook.shared.TestData.sampleSellerEmail
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.utils.plnDto

class OfferEndpointSpec(private val rest: TestRestTemplate) : BaseIntegrationSpec({
    val domain = OfferDomainAbility(rest)
    val authAbility = AuthAbility(rest)
    val categoryAbility = CategoryDomainAbility(rest)
    val shippingMethodAbility = ShippingMethodDomainAbility(rest)

    should("add offer") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin()
        val userToken = authAbility.getTokenForNewUser()
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val requestBody = CreateOfferRequest(
            book = CreateOfferRequest.Book(
                author = "author",
                title = "title",
                isbn = "1234567890",
                condition = "NEW"
            ),
            description = "description",
            category = CreateOfferRequest.Category(categoryId),
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "10.09".plnDto())),
            initialStock = 100
        )

        // when
        val response = domain.createOffer(requestBody, userToken)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson """
            {
                "versionExpireDate": null,
                "book": {
                    "author": "author",
                    "title": "title",
                    "isbn": "1234567890",
                    "condition": "NEW"
                },
                "description": "description",
                "images": {
                    "thumbnail": {
                        "url": "http://localhost:8082/api/images/617ae0fd9ad2e061b2464bea"
                    },
                    "allImages": [
                        {
                            "url": "http://localhost:8082/api/images/617ae0fd9ad2e061b2464bea"
                        }
                    ]
                },
                "type": "EXCHANGE_AND_BUY",
                "cost": {
                    "amount": 10.00,
                    "currency": "PLN"
                },
                "location": "Warsaw",
                "shipping": {
                    "shippingMethods": [
                        {
                            "id": "$shippingMethodId",
                            "cost": {
                                "amount": 10.09,
                                "currency": "PLN"
                            }
                        }
                    ],
                    "cheapestMethod": {
                        "id": "$shippingMethodId",
                        "cost": {
                            "amount": 10.09,
                            "currency": "PLN"
                        }
                    }
                },
                "category": {
                    "id": "$categoryId"
                }
            }
        """
    }

    should("return status 401 when trying to add offer without being logged in") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin()
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val requestBody = CreateOfferRequest(
            book = CreateOfferRequest.Book(
                author = "author",
                title = "title",
                isbn = "1234567890",
                condition = "NEW"
            ),
            description = "description",
            category = CreateOfferRequest.Category(categoryId),
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "10.09".plnDto())),
            initialStock = 100
        )

        // when
        val response = domain.createOffer(requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
    }

    should("return status 422 when validation failed while adding offer") {
        // given
        val userToken = authAbility.getTokenForNewUser()
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val requestBody = CreateOfferRequest(
            book = CreateOfferRequest.Book(
                author = "author",
                title = "title",
                isbn = "1234567890",
                condition = "NEW"
            ),
            description = "description",
            category = CreateOfferRequest.Category(sampleCategoryId.raw),
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "10.09".plnDto())),
            initialStock = 100
        )

        // when
        val response = domain.createOffer(requestBody, userToken)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("return status 422 when request body was invalid while adding offer") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin()
        val userToken = authAbility.getTokenForNewUser()
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val requestBody = CreateOfferRequest(
            book = CreateOfferRequest.Book(
                author = "author",
                title = "title",
                isbn = "12345678900",
                condition = "NEW"
            ),
            description = "description",
            category = CreateOfferRequest.Category(categoryId),
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "10.09".plnDto())),
            initialStock = 100
        )

        // when
        val response = domain.createOffer(requestBody, userToken)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("update offer") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin()
        val userToken = authAbility.getTokenForNewUser()
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val offerId = domain.thereIsOffer(
            bookAuthor = "author",
            bookTitle = "title",
            isbn = "1234567890",
            bookCondition = "NEW",
            description = "description",
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "2.00".plnDto())),
            categoryId = categoryId,
            initialStock = 100,
            token = userToken
        ).body!!.id

        val requestBody = UpdateOfferRequest(
            book = UpdateOfferRequest.Book(
                author = "updatedAuthor",
                title = "updatedTitle",
                isbn = "2345678901",
                condition = "PERFECT"
            ),
            description = "updatedDescription",
            type = "BUY_ONLY",
            price = "11.00".plnDto(),
            location = "Bydgoszcz",
            shippingMethods = listOf(UpdateOfferRequest.ShippingMethod(shippingMethodId, "10.00".plnDto())),
            images = UpdateOfferRequest.Images(
                thumbnail = null,
                allImages = emptyList()
            )
        )

        // when
        val response = domain.updateOffer(offerId, requestBody, userToken)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson """
            {
                "versionExpireDate": null,
                "book": {
                    "author": "updatedAuthor",
                    "title": "updatedTitle",
                    "isbn": "2345678901",
                    "condition": "PERFECT"
                },
                "description": "updatedDescription",
                "images": {
                    "thumbnail": null,
                    "allImages": []
                },
                "type": "BUY_ONLY",
                "cost": {
                    "amount": 11.00,
                    "currency": "PLN"
                },
                "location": "Bydgoszcz",
                "shipping": {
                    "shippingMethods": [
                        {
                            "id": "$shippingMethodId",
                            "cost": {
                                "amount": 10.00,
                                "currency": "PLN"
                            }
                        }
                    ],
                    "cheapestMethod": {
                        "id": "$shippingMethodId",
                        "cost": {
                            "amount": 10.00,
                            "currency": "PLN"
                        }
                    }
                },
                "category": {
                    "id": "$categoryId"
                }
            }
        """
    }

    should("return status 401 when trying to update offer without being logged in") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin()
        val userToken = authAbility.getTokenForNewUser()
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val offerId = domain.thereIsOffer(
            bookAuthor = "author",
            bookTitle = "title",
            isbn = "1234567890",
            bookCondition = "NEW",
            description = "description",
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "2.00".plnDto())),
            categoryId = categoryId,
            initialStock = 100,
            token = userToken
        ).body!!.id

        val requestBody = UpdateOfferRequest(
            book = UpdateOfferRequest.Book(
                author = "updatedAuthor",
                title = "updatedTitle",
                isbn = "2345678901",
                condition = "PERFECT"
            ),
            description = "updatedDescription",
            type = "BUY_ONLY",
            price = "11.00".plnDto(),
            location = "Bydgoszcz",
            shippingMethods = listOf(UpdateOfferRequest.ShippingMethod(shippingMethodId, "10.00".plnDto())),
            images = UpdateOfferRequest.Images(
                thumbnail = null,
                allImages = emptyList()
            )
        )

        // when
        val response = domain.updateOffer(offerId, requestBody)

        // then
        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
    }

    should("return status 403 when trying to update offer which doesn't belong to seller") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, email = sampleAdminEmail)
        val userToken = authAbility.getTokenForNewUser(username = sampleSellerUsername, email = sampleSellerEmail)
        val otherUserToken = authAbility.getTokenForNewUser(username = otherSampleSellerUsername, email = otherSampleSellerEmail)
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val offerId = domain.thereIsOffer(
            bookAuthor = "author",
            bookTitle = "title",
            isbn = "1234567890",
            bookCondition = "NEW",
            description = "description",
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "2.00".plnDto())),
            categoryId = categoryId,
            initialStock = 100,
            token = userToken
        ).body!!.id

        val requestBody = UpdateOfferRequest(
            book = UpdateOfferRequest.Book(
                author = "updatedAuthor",
                title = "updatedTitle",
                isbn = "2345678901",
                condition = "PERFECT"
            ),
            description = "updatedDescription",
            type = "BUY_ONLY",
            price = "11.00".plnDto(),
            location = "Bydgoszcz",
            shippingMethods = listOf(UpdateOfferRequest.ShippingMethod(shippingMethodId, "10.00".plnDto())),
            images = UpdateOfferRequest.Images(
                thumbnail = null,
                allImages = emptyList()
            )
        )

        // when
        val response = domain.updateOffer(offerId, requestBody, otherUserToken)

        // then
        response.statusCode shouldBe HttpStatus.FORBIDDEN
    }

    should("return status 422 when validation failed while updating offer") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, email = sampleAdminEmail)
        val userToken = authAbility.getTokenForNewUser(username = sampleSellerUsername, email = sampleSellerEmail)
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val offerId = domain.thereIsOffer(
            bookAuthor = "author",
            bookTitle = "title",
            isbn = "1234567890",
            bookCondition = "NEW",
            description = "description",
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "2.00".plnDto())),
            categoryId = categoryId,
            initialStock = 100,
            token = userToken
        ).body!!.id

        val requestBody = UpdateOfferRequest(
            book = UpdateOfferRequest.Book(
                author = "updatedAuthor",
                title = "updatedTitle",
                isbn = "2345678901",
                condition = "PERFECT"
            ),
            description = "updatedDescription",
            type = "BUY_ONLY",
            price = "11.00".plnDto(),
            location = "Bydgoszcz",
            shippingMethods = listOf(UpdateOfferRequest.ShippingMethod(sampleCategoryId.raw, "10.00".plnDto())),
            images = UpdateOfferRequest.Images(
                thumbnail = null,
                allImages = emptyList()
            )
        )

        // when
        val response = domain.updateOffer(offerId, requestBody, userToken)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("return status 422 when request body was invalid while updating offer") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, email = sampleAdminEmail)
        val userToken = authAbility.getTokenForNewUser(username = sampleSellerUsername, email = sampleSellerEmail)
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val offerId = domain.thereIsOffer(
            bookAuthor = "author",
            bookTitle = "title",
            isbn = "1234567890",
            bookCondition = "NEW",
            description = "description",
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "2.00".plnDto())),
            categoryId = categoryId,
            initialStock = 100,
            token = userToken
        ).body!!.id

        val requestBody = UpdateOfferRequest(
            book = UpdateOfferRequest.Book(
                author = "updatedAuthor",
                title = "updatedTitle",
                isbn = "23456789010",
                condition = "PERFECT"
            ),
            description = "updatedDescription",
            type = "BUY_ONLY",
            price = "11.00".plnDto(),
            location = "Bydgoszcz",
            shippingMethods = listOf(UpdateOfferRequest.ShippingMethod(shippingMethodId, "10.00".plnDto())),
            images = UpdateOfferRequest.Images(
                thumbnail = null,
                allImages = emptyList()
            )
        )

        // when
        val response = domain.updateOffer(offerId, requestBody, userToken)

        // then
        response.statusCode shouldBe HttpStatus.UNPROCESSABLE_ENTITY
    }

    should("return status 404 when trying to update non existing offer") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, email = sampleAdminEmail)
        val userToken = authAbility.getTokenForNewUser(username = sampleSellerUsername, email = sampleSellerEmail)
        categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id

        val requestBody = UpdateOfferRequest(
            book = UpdateOfferRequest.Book(
                author = "updatedAuthor",
                title = "updatedTitle",
                isbn = "2345678901",
                condition = "PERFECT"
            ),
            description = "updatedDescription",
            type = "BUY_ONLY",
            price = "11.00".plnDto(),
            location = "Bydgoszcz",
            shippingMethods = listOf(UpdateOfferRequest.ShippingMethod(shippingMethodId, "10.00".plnDto())),
            images = UpdateOfferRequest.Images(
                thumbnail = null,
                allImages = emptyList()
            )
        )

        // when
        val response = domain.updateOffer(sampleOfferId.raw, requestBody, userToken)

        // then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
    }

    should("get offer") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin()
        val userToken = authAbility.getTokenForNewUser()
        val categoryId = categoryAbility.thereIsCategory(token = adminToken).body!!.id
        val shippingMethodId = shippingMethodAbility.thereIsShippingMethod().body!!.id
        val offerId = domain.thereIsOffer(
            bookAuthor = "author",
            bookTitle = "title",
            isbn = "1234567890",
            bookCondition = "NEW",
            description = "description",
            type = "EXCHANGE_AND_BUY",
            price = "10.00".plnDto(),
            location = "Warsaw",
            shippingMethods = listOf(CreateOfferRequest.ShippingMethod(shippingMethodId, "2.00".plnDto())),
            categoryId = categoryId,
            initialStock = 100,
            token = userToken
        ).body!!.id

        // when
        val response = domain.getOffer(offerId)

        // then
        response.statusCode shouldBe HttpStatus.OK
        response.body!! shouldEqualSpecifiedJson """
            {
                "versionExpireDate": null,
                "book": {
                    "author": "author",
                    "title": "title",
                    "isbn": "1234567890",
                    "condition": "NEW"
                },
                "description": "description",
                "images": {
                    "thumbnail": {
                        "url": "http://localhost:8082/api/images/617ae0fd9ad2e061b2464bea"
                    },
                    "allImages": [
                        {
                            "url": "http://localhost:8082/api/images/617ae0fd9ad2e061b2464bea"
                        }
                    ]
                },
                "type": "EXCHANGE_AND_BUY",
                "cost": {
                    "amount": 10.00,
                    "currency": "PLN"
                },
                "location": "Warsaw",
                "shipping": {
                    "shippingMethods": [
                        {
                            "id": "$shippingMethodId",
                            "cost": {
                                "amount": 2.00,
                                "currency": "PLN"
                            }
                        }
                    ],
                    "cheapestMethod": {
                        "id": "$shippingMethodId",
                        "cost": {
                            "amount": 2.00,
                            "currency": "PLN"
                        }
                    }
                },
                "category": {
                    "id": "$categoryId"
                }
            }
        """
    }

    should("return status 404 when trying to get non existing offer") {
        // when
        val response = domain.getOffer(sampleOfferId.raw)

        // then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
    }
})
