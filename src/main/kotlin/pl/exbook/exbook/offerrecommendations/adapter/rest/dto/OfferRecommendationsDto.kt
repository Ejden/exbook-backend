package pl.exbook.exbook.offerrecommendations.adapter.rest.dto

import pl.exbook.exbook.shared.dto.MoneyDto

data class OfferRecommendationsDto(
    val recommendations: List<OfferRecommendationDto>
)

data class OfferRecommendationDto(
    val id: String,
    val book: Book,
    val images: Images,
    val type: String,
    val seller: Seller,
    val price: MoneyDto?
) {
    data class Book(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: String
    )

    data class Images(
        val thumbnail: Image?,
        val allImages: List<Image>
    )

    data class Seller(
        val id: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val grade: Double
    )

    data class Category(val id: String)

    data class Image(val url: String)
}
