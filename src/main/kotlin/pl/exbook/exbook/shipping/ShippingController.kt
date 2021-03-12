package pl.exbook.exbook.shipping

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/offers")
class ShippingController {

    @GetMapping("{offerId}/shipping")
    fun getOfferShippingMethods(@PathVariable offerId: String) : ShippingMethod? {
        return null
    }
}