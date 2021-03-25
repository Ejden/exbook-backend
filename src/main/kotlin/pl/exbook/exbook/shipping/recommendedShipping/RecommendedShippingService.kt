package pl.exbook.exbook.shipping.recommendedShipping

import org.springframework.stereotype.Service

@Service
class RecommendedShippingService(
    val recommendedShippingRepository: RecommendedShippingRepository
) {

    fun getAllRecommendedShippingMethods() : Collection<RecommendedShippingMethod> {
        return recommendedShippingRepository.findAll();
    }

    fun addRecommendedShippingMethod(request: NewRecommendedShippingMethodRequest) : RecommendedShippingMethod? {
        val newShippingMethod = RecommendedShippingMethod(request.methodName, request.recommendedPrice)
        return recommendedShippingRepository.save(newShippingMethod)
    }
}