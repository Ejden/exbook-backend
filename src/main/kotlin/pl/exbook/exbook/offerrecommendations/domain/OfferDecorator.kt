package pl.exbook.exbook.offerrecommendations.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.user.domain.User

@Service
class OfferDecorator {
    fun decorateOffer(offer: Offer, seller: User): OfferRecommendation = OfferRecommendation(
        id = offer.id,
        book = with(offer.book) {
            OfferRecommendation.Book(
                author = author,
                title = title,
                isbn = isbn,
                condition = condition
            )
        },
        images = OfferRecommendation.Images(
            thumbnail = offer.images.thumbnail?.let { OfferRecommendation.Image(it.url) },
            allImages = offer.images.allImages.map { OfferRecommendation.Image(it.url) }
        ),
        type = offer.type,
        seller = OfferRecommendation.Seller(
            id = seller.id,
            username = seller.username,
            firstName = seller.firstName,
            lastName = seller.lastName,
            grade = seller.grade
        ),
        price = offer.price
    )
}
