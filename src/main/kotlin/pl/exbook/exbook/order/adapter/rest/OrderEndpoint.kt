package pl.exbook.exbook.order.adapter.rest

import mu.KLogging
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
import pl.exbook.exbook.shared.ContentType

@RestController
@RequestMapping("api")
class OrderEndpoint(private val orderFacade: OrderFacade) {
    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("orders")
    fun getUserOrders(
        @RequestParam("p") page: Int?,
        @RequestParam("size") itemsPerPage: Int?,
        user: UsernamePasswordAuthenticationToken
    ): Page<OrderDto> {
        return orderFacade.getUserOrders(user.name, itemsPerPage, page).map { it.toDto() }
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("orders/snippet")
    fun getUserOrdersSnippets(
        @RequestParam("p") page: Int?,
        @RequestParam("size") itemsPerPage: Int?,
        user: UsernamePasswordAuthenticationToken
    ) : Page<OrderSnippetDto> {
        return orderFacade.getUserOrdersSnippets(user.name, itemsPerPage, page).map { it.toDto() }
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("orders/{orderId}")
    fun getUserOrder(@PathVariable orderId: OrderId, user: UsernamePasswordAuthenticationToken): OrderSnippetDto {
        return orderFacade.getOrderSnippet(orderId, user.name).toDto()
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

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("sale/orders/snippet", produces = [ContentType.V1])
    fun getSellerOrdersSnippets(
        @RequestParam("p") page: Int?,
        @RequestParam("size") itemsPerPage: Int?,
        user: UsernamePasswordAuthenticationToken
    ): Page<OrderSnippetDto> {
        return orderFacade.getSellerOrdersSnippets(user.name, itemsPerPage, page).map { it.toDto() }
    }

    companion object : KLogging()
}

data class OrderDto(
    val id: String,
    val buyer: BuyerDto,
    val seller: SellerDto,
    val shipping: ShippingDto,
    val items: List<OrderItemDto>,
    val orderType: String,
    val exchangeBooks: List<ExchangeBookDto>,
    val orderDate: Instant,
    val status: String,
    val totalCost: MoneyDto,
    val note: String
) {
    data class BuyerDto(val id: String)

    data class OrderItemDto(
        val offerId: String,
        val quantity: Long,
        val cost: MoneyDto?
    )

    data class SellerDto(val id: String)

    data class ShippingDto(val id: String)

    data class ExchangeBookDto(
        val id: String,
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: String,
        val quantity: Int
    )
}

data class OrderSnippetDto(
    val id: String,
    val buyer: BuyerDto,
    val seller: SellerDto,
    val shipping: ShippingDto,
    val items: List<OrderItemDto>,
    val orderType: String,
    val exchangeBooks: List<ExchangeBookDto>,
    val orderDate: Instant,
    val status: String,
    val totalCost: MoneyDto,
    val note: String
) {
    data class BuyerDto(
        val id: String,
        val name: String,
        val firstName: String,
        val lastName: String
    )

    data class SellerDto(
        val id: String,
        val name: String,
        val firstName: String,
        val lastName: String
    )

    data class ShippingDto(
        val id: String,
        val methodName: String,
        val methodType: String,
        val shippingAddress: ShippingAddressDto?,
        val pickupPoint: PickupPointDto?,
        val cost: CostDto
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

    data class OrderItemDto(
        val offerId: String,
        val book: BookDto,
        val images: ImagesDto,
        val quantity: Long,
        val cost: MoneyDto?
    )

    data class BookDto(
        val author: String,
        val title: String,
    )

    data class ImagesDto(val thumbnail: ImageDto?)

    data class ImageDto(val url: String)

    data class CostDto(val finalCost: MoneyDto)

    data class ExchangeBookDto(
        val author: String,
        val title: String,
        val isbn: String?,
        val condition: String,
        val quantity: Int
    )
}

private fun Order.toDto() = OrderDto(
    id = this.id.raw,
    buyer = OrderDto.BuyerDto(this.buyer.id.raw),
    seller = OrderDto.SellerDto(this.seller.id.raw),
    shipping = OrderDto.ShippingDto(this.shipping.id.raw),
    items = this.items.map { it.toDto() },
    orderType = this.orderType.name,
    exchangeBooks = this.exchangeBooks.map { it.toDto() },
    orderDate = this.orderDate,
    status = this.status.name,
    totalCost = this.totalCost.toDto(),
    note = this.note
)

private fun Order.OrderItem.toDto() = OrderDto.OrderItemDto(
    offerId = this.offerId.raw,
    quantity = this.quantity,
    cost = this.cost?.toDto()
)

private fun Order.ExchangeBook.toDto() = OrderDto.ExchangeBookDto(
    id = this.id.raw,
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name,
    quantity = this.quantity
)

private fun OrderSnippet.toDto() = OrderSnippetDto(
    id = this.id.raw,
    buyer = this.buyer.toDto(),
    seller = this.seller.toDto(),
    shipping = this.shipping.toDto(),
    items = this.items.map { it.toDto() },
    orderType = this.orderType.name,
    exchangeBooks = this.exchangeBooks.map {
        OrderSnippetDto.ExchangeBookDto(
            author = it.author,
            title = it.title,
            isbn = it.isbn,
            condition = it.condition.name,
            quantity = it.quantity
        )
    },
    orderDate = this.orderDate,
    status = this.status.name,
    totalCost = this.totalCost.toDto(),
    note = this.note
)

private fun OrderSnippet.Buyer.toDto() = OrderSnippetDto.BuyerDto(
    id = this.id.raw,
    name = this.name,
    firstName = this.firstName,
    lastName = this.lastName
)

private fun OrderSnippet.Seller.toDto() = OrderSnippetDto.SellerDto(
    id = this.id.raw,
    name = this.name,
    firstName = this.firstName,
    lastName = this.lastName
)

private fun OrderSnippet.Shipping.toDto() = OrderSnippetDto.ShippingDto(
    id = this.id.raw,
    methodName = this.methodName,
    methodType = this.methodType.name,
    shippingAddress = this.shippingAddress?.let {
        OrderSnippetDto.ShippingAddressDto(
            firstAndLastName = it.firstAndLastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            address = it.address,
            postalCode = it.postalCode,
            city = it.city,
            country = it.country
        )
    },
    pickupPoint = this.pickupPoint?.let {
        OrderSnippetDto.PickupPointDto(
            firstAndLastName = it.firstAndLastName,
            phoneNumber = it.phoneNumber,
            email = it.email,
            pickupPointId = it.pickupPointId.raw
        )
    },
    cost = OrderSnippetDto.CostDto(this.cost.finalCost.toDto())
)

private fun OrderSnippet.OrderItem.toDto() = OrderSnippetDto.OrderItemDto(
    offerId = this.offerId.raw,
    book = this.book.toDto(),
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
