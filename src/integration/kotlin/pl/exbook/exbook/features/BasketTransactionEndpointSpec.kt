package pl.exbook.exbook.features

import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import pl.exbook.exbook.BaseIntegrationSpec
import pl.exbook.exbook.ability.AuthAbility
import pl.exbook.exbook.ability.BasketDomainAbility
import pl.exbook.exbook.ability.BasketTransactionDomainAbility
import pl.exbook.exbook.ability.CategoryDomainAbility
import pl.exbook.exbook.ability.OfferDomainAbility
import pl.exbook.exbook.ability.ShippingMethodDomainAbility
import pl.exbook.exbook.ability.UserDomainAbility
import pl.exbook.exbook.baskettransaction.adapter.rest.dto.PreviewPurchaseRequest
import pl.exbook.exbook.offer.adapter.rest.dto.CreateOfferRequest
import pl.exbook.exbook.shared.TestData.sampleAdminEmail
import pl.exbook.exbook.shared.TestData.sampleAdminUsername
import pl.exbook.exbook.shared.TestData.sampleBuyerEmail
import pl.exbook.exbook.shared.TestData.sampleBuyerFirstName
import pl.exbook.exbook.shared.TestData.sampleBuyerLastName
import pl.exbook.exbook.shared.TestData.sampleBuyerPhoneNumber
import pl.exbook.exbook.shared.TestData.sampleBuyerUsername
import pl.exbook.exbook.shared.TestData.sampleSellerEmail
import pl.exbook.exbook.shared.TestData.sampleSellerUsername
import pl.exbook.exbook.utils.plnDto

class BasketTransactionEndpointSpec(private val rest: TestRestTemplate) : BaseIntegrationSpec({
    val domain = BasketTransactionDomainAbility(rest)
    val authAbility = AuthAbility(rest)
    val offerAbility = OfferDomainAbility(rest)
    val categoryAbility = CategoryDomainAbility(rest)
    val shippingMethodAbility = ShippingMethodDomainAbility(rest)
    val basketAbility = BasketDomainAbility(rest)
    val userAbility = UserDomainAbility(rest)

    should("preview purchase without shipping") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, email = sampleAdminEmail)
        val category = categoryAbility.thereIsCategory(token = adminToken).body!!
        val shippingMethod = shippingMethodAbility.thereIsShippingMethod().body!!
        val sellerToken = authAbility.getTokenForNewUser(username = sampleSellerUsername, email = sampleSellerEmail)
        val seller = userAbility.getUserFromToken(sellerToken).body!!
        val offer = offerAbility.thereIsOffer(
            categoryId = category.id,
            shippingMethods = listOf(
                CreateOfferRequest.ShippingMethod(
                    id = shippingMethod.id,
                    price = shippingMethod.defaultCost.cost
                )
            ),
            token = sellerToken
        ).body!!
        val buyerToken = authAbility.getTokenForNewUser(username = sampleBuyerUsername, email = sampleBuyerEmail)
        val buyer = userAbility.getUserFromToken(buyerToken).body!!
        basketAbility.thereIsItemInBasket(offerId = offer.id, orderType = "BUY", token = buyerToken)
        val request = PreviewPurchaseRequest(
            orders = listOf()
        )

        // when
        val result = domain.previewPurchase(requestBody = request, token = buyerToken)

        // then
        result.statusCode shouldBe HttpStatus.OK
        // language=JSON
        result.body!! shouldEqualSpecifiedJson """
            {
              "buyer": {
                "id": "${buyer.id}"
              },
              "orders": [
                {
                  "orderType": "BUY",
                  "seller": {
                    "id": "${seller.id}",
                    "firstName": "Tom",
                    "lastName": "Riddle",
                    "username": "seller-username"
                  },
                  "items": [
                    {
                      "offer": {
                        "id": "${offer.id}",
                        "price": {
                          "amount": 10.00,
                          "currency": "PLN"
                        },
                        "book": {
                          "author": "Jan",
                          "title": "Jan na drzewie",
                          "condition": "NEW",
                          "isbn": "1234567890"
                        },
                        "images": {
                          "thumbnail": null,
                          "allImages": []
                        }
                      },
                      "quantity": 1,
                      "totalPrice": {
                        "amount": 10.00,
                        "currency": "PLN"
                      }
                    }
                  ],
                  "exchangeBooks": [],
                  "shipping": null,
                  "totalOffersPrice": {
                    "amount": 10.00,
                    "currency": "PLN"
                  },
                  "totalPrice": {
                    "amount": 10.00,
                    "currency": "PLN"
                  }
                }
              ],
              "totalOffersPrice": {
                "amount": 10.00,
                "currency": "PLN"
              },
              "totalPrice": {
                "amount": 10.00,
                "currency": "PLN"
              }
            }
        """
    }

    should("preview purchase with shipping") {
        // given
        val adminToken = authAbility.getTokenForNewAdmin(username = sampleAdminUsername, email = sampleAdminEmail)
        val category = categoryAbility.thereIsCategory(token = adminToken).body!!
        val shippingMethod = shippingMethodAbility.thereIsShippingMethod(pickupMethod = true).body!!
        val sellerToken = authAbility.getTokenForNewUser(username = sampleSellerUsername, email = sampleSellerEmail)
        val seller = userAbility.getUserFromToken(sellerToken).body!!
        val offer = offerAbility.thereIsOffer(
            categoryId = category.id,
            shippingMethods = listOf(
                CreateOfferRequest.ShippingMethod(
                    id = shippingMethod.id,
                    price = "5.00".plnDto()
                )
            ),
            token = sellerToken
        ).body!!
        val buyerToken = authAbility.getTokenForNewUser(username = sampleBuyerUsername, email = sampleBuyerEmail)
        val buyer = userAbility.getUserFromToken(buyerToken).body!!
        basketAbility.thereIsItemInBasket(offerId = offer.id, orderType = "BUY", quantity = 2, token = buyerToken)
        val request = PreviewPurchaseRequest(
            orders = listOf(
                PreviewPurchaseRequest.Order(
                    sellerId = seller.id,
                    orderType = "BUY",
                    shipping = PreviewPurchaseRequest.Shipping(
                        shippingMethodId = shippingMethod.id,
                        shippingAddress = null,
                        pickupPoint = PreviewPurchaseRequest.PickupPoint(
                            firstAndLastName = "$sampleBuyerFirstName $sampleBuyerLastName",
                            phoneNumber = sampleBuyerPhoneNumber,
                            email = sampleBuyerEmail,
                            pickupPointId = "WAW1112"
                        )
                    )
                )
            )
        )

        // when
        val result = domain.previewPurchase(requestBody = request, token = buyerToken)

        // then
        result.statusCode shouldBe HttpStatus.OK
        // language=JSON
        result.body!! shouldEqualSpecifiedJson """
            {
              "buyer": {
                "id": "${buyer.id}"
              },
              "orders": [
                {
                  "orderType": "BUY",
                  "seller": {
                    "id": "${seller.id}",
                    "firstName": "Tom",
                    "lastName": "Riddle",
                    "username": "seller-username"
                  },
                  "items": [
                    {
                      "offer": {
                        "id": "${offer.id}",
                        "price": {
                          "amount": 10.00,
                          "currency": "PLN"
                        },
                        "book": {
                          "author": "Jan",
                          "title": "Jan na drzewie",
                          "condition": "NEW",
                          "isbn": "1234567890"
                        },
                        "images": {
                          "thumbnail": null,
                          "allImages": []
                        }
                      },
                      "quantity": 2,
                      "totalPrice": {
                        "amount": 20.00,
                        "currency": "PLN"
                      }
                    }
                  ],
                  "exchangeBooks": [],
                  "shipping": {
                    "shippingMethod": {
                      "id": "${shippingMethod.id}",
                      "methodName": "shipping-method",
                      "price": {
                        "finalPrice": {
                          "amount": 5.00,
                          "currency": "PLN"
                        }
                      }
                    },
                    "pickupPoint": {
                      "firstAndLastName": "buyer-first-name buyer-last-name",
                      "phoneNumber": "123412123",
                      "email": "ad@gmai.com",
                      "pickupPointId": "WAW1112"
                    },
                    "shippingAddress": null
                  },
                  "totalOffersPrice": {
                    "amount": 20.00,
                    "currency": "PLN"
                  },
                  "totalPrice": {
                    "amount": 25.00,
                    "currency": "PLN"
                  }
                }
              ],
              "totalOffersPrice": {
                "amount": 20.00,
                "currency": "PLN"
              },
              "totalPrice": {
                "amount": 25.00,
                "currency": "PLN"
              }
            }
        """
    }
})
