package pl.exbook.exbook.shipping.recommendedShipping

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/shipping")
class RecommendedShippingController(
    val recommendedShippingService: RecommendedShippingService
) {

    @GetMapping
    fun getAllRecommendedShippingMethods() : Collection<RecommendedShippingMethod> {
        return recommendedShippingService.getAllRecommendedShippingMethods()
    }

    @PostMapping
    fun addRecommendedShippingMethod(@RequestBody request: NewRecommendedShippingMethodRequest) : RecommendedShippingMethod? {
        return recommendedShippingService.addRecommendedShippingMethod(request)
    }
}

data class NewRecommendedShippingMethodRequest(
    val methodName: String,
    val recommendedPrice: Int
)