package pl.exbook.exbook.shipping.recommendedShipping

import org.springframework.data.mongodb.core.mapping.Document

@Document("recommended-shipping-methods")
class RecommendedShippingMethod(
    var methodName: String,
    var recommendedPrice: Int) {

    var id: String? = null
}