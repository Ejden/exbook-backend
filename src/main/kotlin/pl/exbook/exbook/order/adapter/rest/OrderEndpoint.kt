package pl.exbook.exbook.order.adapter.rest

import mu.KLogging
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.order.OrderFacade
import pl.exbook.exbook.order.domain.Order
import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDto
import pl.exbook.exbook.user.UserNotFoundException
import java.time.Instant

@RestController
@RequestMapping("api/order")
class OrderEndpoint(private val orderFacade: OrderFacade) {

    companion object : KLogging()

    @PreAuthorize("isFullyAuthenticated()")
    @PostMapping
    fun placeOrder(@RequestBody newOrder: NewOrderDto, user: UsernamePasswordAuthenticationToken?): OrderDto {
        return when {
            user != null -> orderFacade.placeOrder(newOrder, user.name).toDto()
            else ->  {
                logger.warn { "Non logged user tried to place order" }
                throw UserNotFoundException("Non logged user tried to place order")
            }
        }
    }
}

data class NewOrderDto(
    val items: List<OrderItemDto>
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
}

data class OrderDto(
    val id: String,
    val buyer: BuyerDto,
    val items: List<OrderItemDto>,
    val orderDate: Instant,
    val totalPrice: MoneyDto,
    val returned: Boolean
) {
    data class BuyerDto(val id: String)

    data class OrderItemDto(
        val offerId: String,
        val seller: SellerDto,
        val orderType: String,
        val exchangeBook: ExchangeBookDto?,
        val quantity: Int,
        val price: MoneyDto?
    )

    data class SellerDto(val id: String)

    data class ExchangeBookDto(
        val author: String,
        val title: String,
        val isbn: Long?,
        val condition: String
    )
}

private fun Order.toDto() = OrderDto(
    id = this.id!!.raw,
    buyer = OrderDto.BuyerDto(this.buyer.id.raw),
    items = this.items.map { it.toDto() },
    orderDate = this.orderDate,
    totalPrice = Money.sum(this.items.map { it.price }).toDto(),
    returned = this.returned
)

private fun Order.OrderItem.toDto() = OrderDto.OrderItemDto(
    offerId = this.offerId.raw,
    seller = OrderDto.SellerDto(this.seller.id.raw),
    orderType = this.orderType.name,
    exchangeBook = this.exchangeBook?.toDto(),
    quantity = this.quantity,
    price = this.price?.toDto()
)

private fun Order.ExchangeBook.toDto() = OrderDto.ExchangeBookDto(
    author = this.author,
    title = this.title,
    isbn = this.isbn,
    condition = this.condition.name
)
