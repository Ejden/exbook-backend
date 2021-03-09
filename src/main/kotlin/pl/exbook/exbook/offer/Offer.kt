package pl.exbook.exbook.offer

import pl.exbook.exbook.user.User

class Offer (
    var id: String?,
    var book: Book?,
    var images: Images,
    var description: String?,
    var seller: User?
) {

    fun toOfferDto() : OfferDto {
        return OfferDto(
            id = this.id!!,
            book = this.book!!,
            images = this.images,
            description = this.description,
            seller = OfferDto.Seller(seller?.id!!)
        )
    }

}

class Book(
    var author: String,
    var title: String,
    var ISBN: Long?,
    var condition: Condition
)


class Images {
    var thumbnailUrl: String? = null
    var images = listOf<String>()
}

enum class Condition {
    NEW, PERFECT, LIGHTLY_USED, MODERATELY_USED, BAD
}
