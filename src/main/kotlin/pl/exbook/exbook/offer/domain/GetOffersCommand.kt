package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.shared.CategoryId
import java.math.BigDecimal

data class GetOffersCommand(
    val searchingPhrase: String,
    val bookConditions: List<Offer.Condition>?,
    val offerType: List<Offer.Type>?,
    val priceFrom: BigDecimal?,
    val priceTo: BigDecimal?,
    val location: String?,
    val categoryId: CategoryId?,
    val offersPerPage: Int?,
    val page: Int?,
    val sorting: String?
)
