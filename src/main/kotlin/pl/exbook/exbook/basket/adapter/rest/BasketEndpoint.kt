package pl.exbook.exbook.basket.adapter.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.basket.domain.AddItemToBasketCommand
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.DetailedBasket
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto

@RestController
@RequestMapping("api/basket")
class BasketEndpoint(private val basketFacade: BasketFacade) {

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping(produces = [ContentType.V1])
    fun getBasket(user: UsernamePasswordAuthenticationToken): DetailedBasketDto {
        return basketFacade.getDetailedUserBasket(user.name).toDto()
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PutMapping(produces = [ContentType.V1])
    fun addItemToBasket(
        @RequestBody request: AddItemToBasketRequest,
        user: UsernamePasswordAuthenticationToken
    ): BasketDto {
        return basketFacade.addItemToBasket(user.name, request.toCommand()).toDto()
    }

    @PreAuthorize("isFullyAuthenticated()")
    @DeleteMapping("{offerId}", produces = [ContentType.V1])
    fun removeItemFromBasket(
        @PathVariable offerId: OfferId,
        user: UsernamePasswordAuthenticationToken
    ): BasketDto {
        return basketFacade.removeItemFromBasket(user.name, offerId).toDto()
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping("{offerId}")
    fun changeItemQuantityInBasket(
        @PathVariable offerId: OfferId,
        @RequestBody request: ChangeItemQuantityRequest,
        user: UsernamePasswordAuthenticationToken
    ): BasketDto {
        return basketFacade.changeItemQuantityInBasket(user.name, offerId, request.newQuantity).toDto()
    }
}

data class ChangeItemQuantityRequest(
    val newQuantity: Long
)

data class AddItemToBasketRequest(
    val offerId: String,
    val quantity: Long
)

data class BasketDto(
    val id: String,
    val userId: String,
    val items: List<ItemDto>,
    val totalOffersCost: MoneyDto
) {
    data class ItemDto(
        val offerId: String,
        val offerPrice: MoneyDto,
        val quantity: Long
    )
}

data class DetailedBasketDto(
    val id: String,
    val userId: String,
    val items: List<ItemDto>,
    val totalOffersCost: MoneyDto
) {
    data class ItemDto(
        val offer: OfferDto,
        val quantity: Long,
        val price: MoneyDto
    )

    data class OfferDto(
        val id: String,
        val price: MoneyDto,
        val book: BookDto,
        val images: ImagesDto,
        val seller: SellerDto
    )

    data class BookDto(
        val author: String,
        val title: String
    )

    data class ImagesDto(
        val thumbnail: ImageDto?,
        val otherImages: Collection<ImageDto>
    )

    data class ImageDto(val url: String)

    data class SellerDto(
        val id: String,
        val firstName: String,
        val lastName: String
    )
}

private fun Basket.toDto() = BasketDto(
    id = this.id.raw,
    userId = this.userId.raw,
    items = this.items.map { it.toDto() },
    totalOffersCost = this.totalOffersCost().toDto()
)

private fun Basket.Item.toDto() = BasketDto.ItemDto(
    offerId = this.offerId.raw,
    offerPrice = this.offerPrice.toDto(),
    quantity = this.quantity
)

private fun AddItemToBasketRequest.toCommand() = AddItemToBasketCommand(
    offerId = OfferId(this.offerId),
    quantity = this.quantity
)

private fun DetailedBasket.toDto() = DetailedBasketDto(
    id = this.id.raw,
    userId = this.userId.raw,
    items = (this.items as List<DetailedBasket.Item>).map { it.toDto() },
    totalOffersCost = totalOffersCost().toDto()
)

private fun DetailedBasket.Item.toDto() = DetailedBasketDto.ItemDto(
    offer = DetailedBasketDto.OfferDto(
        id = this.offer.id.raw,
        price = this.offer.price.toDto(),
        book = this.offer.book.toDto(),
        images = this.offer.images.toDto(),
        seller = this.offer.seller.toDto()
    ),
    quantity = this.quantity,
    price = (this.offer.price * this.quantity).toDto()
)

private fun DetailedBasket.Book.toDto() = DetailedBasketDto.BookDto(
    author = this.author,
    title = this.title
)

private fun DetailedBasket.Images.toDto() = DetailedBasketDto.ImagesDto(
    thumbnail = this.thumbnail?.let { DetailedBasketDto.ImageDto(it.url) },
    otherImages = this.otherImages.map { DetailedBasketDto.ImageDto(it.url) }
)

private fun DetailedBasket.Seller.toDto() = DetailedBasketDto.SellerDto(
    id = this.id.raw,
    firstName = this.firstName,
    lastName = this.lastName
)
