package pl.exbook.exbook.basket.adapter.rest

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
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
import pl.exbook.exbook.basket.adapter.rest.dto.AddExchangeBookMapper
import pl.exbook.exbook.basket.adapter.rest.dto.AddExchangeBookToBasketRequest
import pl.exbook.exbook.basket.adapter.rest.dto.AddItemToBasketMapper
import pl.exbook.exbook.basket.adapter.rest.dto.AddItemToBasketRequest
import pl.exbook.exbook.basket.adapter.rest.dto.BasketDto
import pl.exbook.exbook.basket.adapter.rest.dto.BasketMapper
import pl.exbook.exbook.basket.adapter.rest.dto.ChangeItemQuantityMapper
import pl.exbook.exbook.basket.adapter.rest.dto.DetailedBasketMapper
import pl.exbook.exbook.basket.adapter.rest.dto.ChangeItemQuantityRequest
import pl.exbook.exbook.basket.adapter.rest.dto.DetailedBasketDto
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.ExchangeBookId
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.UserId
import pl.exbook.exbook.util.callhandler.handleRequest

@RestController
@RequestMapping("api/basket")
class BasketEndpoint(private val basketFacade: BasketFacade) {
    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping(produces = [ContentType.V1])
    fun getBasket(user: UsernamePasswordAuthenticationToken): ResponseEntity<DetailedBasketDto> = handleRequest(
        mapper = DetailedBasketMapper,
        call = { basketFacade.getDetailedUserBasket(user.name) },
        response = { ok(it) }
    )

    @PreAuthorize("isFullyAuthenticated()")
    @PutMapping(produces = [ContentType.V1])
    fun addItemToBasket(
        @RequestBody request: AddItemToBasketRequest,
        user: UsernamePasswordAuthenticationToken
    ): ResponseEntity<BasketDto> = handleRequest(
        mapper = AddItemToBasketMapper(user.name),
        requestBody = request,
        call = { basketFacade.addItemToBasket(it) },
        response = { ok(it) }
    )

    @PreAuthorize("isFullyAuthenticated()")
    @DeleteMapping("{orderType}/{offerId}", produces = [ContentType.V1])
    fun removeItemFromBasket(
        @PathVariable orderType: Order.OrderType,
        @PathVariable offerId: OfferId,
        user: UsernamePasswordAuthenticationToken
    ): ResponseEntity<BasketDto> = handleRequest(
        mapper = BasketMapper,
        call = { basketFacade.removeItemFromBasket(user.name, offerId, orderType) },
        response = { ok(it) }
    )

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping("{offerId}")
    fun changeItemQuantityInBasket(
        @PathVariable offerId: OfferId,
        @RequestBody request: ChangeItemQuantityRequest,
        user: UsernamePasswordAuthenticationToken
    ): ResponseEntity<BasketDto> = handleRequest(
        mapper = ChangeItemQuantityMapper(offerId, user.name),
        requestBody = request,
        call = { basketFacade.changeItemQuantityInBasket(it) },
        response = { ok(it) }
    )

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping("/sellers/{sellerId}/books", consumes = [ContentType.V1], produces = [ContentType.V1])
    fun addExchangeBookToBasket(
        @PathVariable sellerId: UserId,
        @RequestBody request: AddExchangeBookToBasketRequest,
        user: UsernamePasswordAuthenticationToken
    ): ResponseEntity<BasketDto> = handleRequest(
        mapper = AddExchangeBookMapper(sellerId, user.name),
        requestBody = request,
        call = { basketFacade.addExchangeBookToBasket(it) },
        response = { ok(it) }
    )

    @PreAuthorize("isFullyAuthenticated()")
    @DeleteMapping("/sellers/{sellerId}/books/{bookId}", produces = [ContentType.V1])
    fun removeExchangeBookToBasket(
        @PathVariable sellerId: UserId,
        @PathVariable bookId: ExchangeBookId,
        user: UsernamePasswordAuthenticationToken
    ): ResponseEntity<BasketDto> = handleRequest(
        mapper = BasketMapper,
        call = { basketFacade.removeExchangeBookFromBasket(user.name, sellerId, bookId) },
        response = { ok(it) }
    )
}
