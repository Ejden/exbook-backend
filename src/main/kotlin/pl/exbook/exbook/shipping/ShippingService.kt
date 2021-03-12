package pl.exbook.exbook.shipping

import org.springframework.stereotype.Service

@Service
class ShippingService {

    fun getRecommendedShippingMethods() : Collection<Shipping> {
        return listOf()
    }
}