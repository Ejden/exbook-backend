package pl.exbook.exbook.offerrecommendations.adapter.rest.dto

import pl.exbook.exbook.offerrecommendations.domain.OfferRecommendation
import pl.exbook.exbook.shared.dto.toDto
import pl.exbook.exbook.util.mapper.FromDomainMapper

object OfferRecommendationMapper : FromDomainMapper<List<OfferRecommendation>, OfferRecommendationsDto> {
    override fun fromDomain(from: List<OfferRecommendation>): OfferRecommendationsDto = OfferRecommendationsDto(
        recommendations = from.map { it.toDto() }
    )

    private fun OfferRecommendation.toDto() = OfferRecommendationDto(
        id = this.id.raw,
        book = OfferRecommendationDto.Book(
            author = this.book.author,
            title = this.book.title,
            isbn = this.book.isbn,
            condition = this.book.condition.name
        ),
        images = OfferRecommendationDto.Images(
            thumbnail = this.images.thumbnail?.let { OfferRecommendationDto.Image(it.url) },
            allImages = this.images.allImages.map { OfferRecommendationDto.Image(it.url) }
        ),
        type = this.type.name,
        seller = OfferRecommendationDto.Seller(
            id = this.seller.id.raw,
            username = this.seller.username,
            firstName = this.seller.firstName,
            lastName = this.seller.lastName,
            grade = this.seller.grade
        ),
        price = this.price?.toDto()
    )
}
