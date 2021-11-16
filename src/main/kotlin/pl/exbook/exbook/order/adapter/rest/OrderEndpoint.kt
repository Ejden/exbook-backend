package pl.exbook.exbook.order.adapter.rest

import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import pl.exbook.exbook.order.OrderFacade
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.OrderId
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto
import java.time.Instant
import pl.exbook.exbook.order.domain.OrderSnippet


@RestController
@RequestMapping("api")
class OrderEndpoint(
    private val orderFacade: OrderFacade,
) {

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping("orders")
    fun placeOrder(@RequestBody newOrder: NewOrderDto, user: UsernamePasswordAuthenticationToken): OrderDto {
        return orderFacade.placeOrder(newOrder, user.name).toDto()
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("orders")
    fun getUserOrders(
        @RequestParam("p") page: Int?,
        @RequestParam("size") itemsPerPage: Int?,
        user: UsernamePasswordAuthenticationToken
    ): Page<OrderDto> {
        return orderFacade.getUserOrders(user.name, page, itemsPerPage).map { it.toDto() }
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("orders/snippet")
    fun getUserOrdersSnippets(
        @RequestParam("p") page: Int?,
        @RequestParam("size") itemsPerPage: Int?,
        user: UsernamePasswordAuthenticationToken
    ) : Page<OrderSnippetDto> {
        return orderFacade.getUserOrdersSnippets(user.name, page, itemsPerPage).map { it.toDto() }
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("orders/{orderId}")
    fun getUserOrder(@PathVariable orderId: OrderId, user: UsernamePasswordAuthenticationToken): OrderDto {
        return orderFacade.getOrder(orderId, user.name).toDto()
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("sale/orders")
    fun getSellerOrders(
        @RequestParam("p") page: Int?,
        @RequestParam("size") itemsPerPage: Int?,
        user: UsernamePasswordAuthenticationToken
    ): Page<OrderDto> {
        return orderFacade.getSellerOrders(user.name, page, itemsPerPage).map { it.toDto() }
    }

    companion object : KLogging()
}

data class NewOrderDto(
    val items: List<OrderItemDto>,
    val seller: SellerDto,
    val shipping: Shipping
) {
    data class OrderItemDto(
        val offerId: String,
        val orderType: String,
        val exchangeBook: ExchangeBookDto?,
        val quantity: Int
    )

    data class ExchangeBookDto(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: String
    )

    data class Shipping(
        val shippingMethodId: String,
        val shippingAddress: ShippingAddressDto?,
        val pickupPoint: PickupPointDto?
    )

    data class ShippingAddressDto(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val address: String,
        val postalCode: String,
        val city: String,
        val country: String
    )

    data class PickupPointDto(
        val firstAndLastName: String,
        val phoneNumber: String,
        val email: String,
        val pickupPointId: String
    )

    data class SellerDto(val id: String)
}

data class OrderDto(
    val id: String,
    val buyer: BuyerDto,
    val seller: SellerDto,
    val shipping: ShippingDto,
    val items: List<OrderItemDto>,
    val orderDate: Instant,
    val status: String,
    val totalCost: MoneyDto
) {
    data class BuyerDto(val id: String)

    data class OrderItemDto(
        val offerId: String,
        val orderType: String,
        val exchangeBook: ExchangeBookDto?,
        val quantity: Int,
        val cost: MoneyDto?
    )

    data class SellerDto(val id: String)

    data class ShippingDto(val id: String)

    data class ExchangeBookDto(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: String
    )
}

data class OrderSnippetDto(
    val id: String,
    val buyer: BuyerDto,
    val seller: SellerDto,
    val shipping: ShippingDto,
    val items: List<OrderItemDto>,
    val orderDate: Instant,
    val status: String,
    val totalCost: MoneyDto
) {
    data class BuyerDto(val id: String)

    data class SellerDto(
        val id: String,
        val name: String,
        val firstName: String,
        val lastName: String
    )

    data class ShippingDto(
        val id: String,
        val methodName: String,
        val cost: CostDto
    )

    data class OrderItemDto(
        val offerId: String,
        val book: BookDto,
        val orderType: String,
        val images: ImagesDto,
        val quantity: Int,
        val cost: MoneyDto?
    )

    data class BookDto(
        val author: String,
        val title: String
    )

    data class ImagesDto(val thumbnail: ImageDto?)

    data class ImageDto(val url: String)

    data class CostDto(val finalCost: MoneyDto)
}

private fun Order.toDto() = OrderDto(
    id = this.id!!.raw,
    buyer = OrderDto.BuyerDto(this.buyer.id.raw),
    seller = OrderDto.SellerDto(this.seller.id.raw),
    shipping = OrderDto.ShippingDto(this.shipping.id.raw),
    items = this.items.map { it.toDto() },
    orderDate = this.orderDate,
    status = this.status.name,
    totalCost = this.totalCost.toDto()
)

private fun Order.OrderItem.toDto() = OrderDto.OrderItemDto(
    offerId = this.offerId.raw,
    orderType = this.orderType.name,
    exchangeBook = this.exchangeBook?.toDto(),
    quantity = this.quantity,
    cost = this.cost?.toDto()
)

private fun Order.ExchangeBook.toDto() = OrderDto.ExchangeBookDto(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)

private fun OrderSnippet.toDto() = OrderSnippetDto(
    id = this.id.raw,
    buyer = this.buyer.toDto(),
    seller = this.seller.toDto(),
    shipping = this.shipping.toDto(),
    items = this.items.map { it.toDto() },
    orderDate = this.orderDate,
    status = this.status.name,
    totalCost = this.totalCost.toDto()
)

private fun OrderSnippet.Buyer.toDto() = OrderSnippetDto.BuyerDto(this.id.raw)

private fun OrderSnippet.Seller.toDto() = OrderSnippetDto.SellerDto(
    id = this.id.raw,
    name = this.name,
    firstName = this.firstName,
    lastName = this.lastName
)

private fun OrderSnippet.Shipping.toDto() = OrderSnippetDto.ShippingDto(
    id = this.id.raw,
    methodName = this.methodName,
    cost = OrderSnippetDto.CostDto(this.cost.finalCost.toDto())
)

private fun OrderSnippet.OrderItem.toDto() = OrderSnippetDto.OrderItemDto(
    offerId = this.offerId.raw,
    book = this.book.toDto(),
    orderType = this.orderType.name,
    images = this.images.toDto(),
    quantity = this.quantity,
    cost = this.cost?.toDto()
)

private fun OrderSnippet.Book.toDto() = OrderSnippetDto.BookDto(
    author = this.author,
    title = this.title
)

private fun OrderSnippet.Images.toDto() = OrderSnippetDto.ImagesDto(
    thumbnail = this.thumbnail?.let { OrderSnippetDto.ImageDto(it.url) }
)
