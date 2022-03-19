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
import pl.exbook.exbook.basket.adapter.rest.dto.AddExchangeBookToBasketRequest
import pl.exbook.exbook.basket.adapter.rest.dto.AddItemToBasketRequest
import pl.exbook.exbook.basket.adapter.rest.dto.BasketDto
import pl.exbook.exbook.basket.adapter.rest.dto.ChangeItemQuantityRequest
import pl.exbook.exbook.basket.adapter.rest.dto.DetailedBasketDto
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.DetailedBasket
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId

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
        return basketFacade.addItemToBasket(request.toCommand(user.name)).toDto()
    }

    @PreAuthorize("isFullyAuthenticated()")
    @DeleteMapping("{orderType}/{offerId}", produces = [ContentType.V1])
    fun removeItemFromBasket(
        @PathVariable orderType: Order.OrderType,
        @PathVariable offerId: OfferId,
        user: UsernamePasswordAuthenticationToken
    ): BasketDto {
        return basketFacade.removeItemFromBasket(user.name, offerId, orderType).toDto()
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping("{offerId}")
    fun changeItemQuantityInBasket(
        @PathVariable offerId: OfferId,
        @RequestBody request: ChangeItemQuantityRequest,
        user: UsernamePasswordAuthenticationToken
    ): BasketDto {
        return basketFacade.changeItemQuantityInBasket(request.toCommand(offerId, user.name)).toDto()
    }

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping("/sellers/{sellerId}/books", consumes = [ContentType.V1], produces = [ContentType.V1])
    fun addExchangeBookToBasket(
        @PathVariable sellerId: UserId,
        @RequestBody request: AddExchangeBookToBasketRequest,
        user: UsernamePasswordAuthenticationToken
    ): BasketDto = basketFacade.addExchangeBookToBasket(request.toCommand(user.name, sellerId)).toDto()

    @PreAuthorize("isFullyAuthenticated()")
    @DeleteMapping("/sellers/{sellerId}/books/{bookId}", produces = [ContentType.V1])
    fun removeExchangeBookToBasket(
        @PathVariable sellerId: UserId,
        @PathVariable bookId: ExchangeBookId,
        user: UsernamePasswordAuthenticationToken
    ): BasketDto = basketFacade.removeExchangeBookFromBasket(user.name, sellerId, bookId).toDto()
}

private fun Basket.toDto() = BasketDto.fromDomain(this)

private fun DetailedBasket.toDto() = DetailedBasketDto.fromDomain(this)
