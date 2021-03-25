package pl.exbook.exbook.shipping.recommendedShipping

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RecommendedShippingRepository :  MongoRepository<RecommendedShippingMethod, String> {
}