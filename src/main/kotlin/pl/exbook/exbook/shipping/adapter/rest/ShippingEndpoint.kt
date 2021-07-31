package pl.exbook.exbook.shipping.adapter.rest

import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.domain.Cost
import pl.exbook.exbook.shipping.domain.ShippingMethod
import pl.exbook.exbook.util.parseMoneyToString

const val CONTENT_TYPE = "application/vnd.exbook.v1+json"

@RestController
@RequestMapping("api/shipping")
class ShippingEndpoint(
    val shippingFacade: ShippingFacade
) {

    @GetMapping(consumes = [CONTENT_TYPE])
    fun getShippingMethods(): List<ShippingMethodDto> {
        return shippingFacade.getShippingMethods().map { it.toDto() }
    }

    @PostMapping(consumes = [CONTENT_TYPE])
    fun addShippingMethod(@RequestBody requestBody: NewShippingMethod): ShippingMethodDto {
        return shippingFacade.addShippingMethod(requestBody).toDto()
    }
}

data class NewShippingMethod(
    val name: String,
    val defaultCost: String
)

data class ShippingMethodDto(
    val id: String,
    val name: String,
    val defaultCost: CostDto
) {
    data class CostDto(
        val value: String,
        val currency: String
    )
}

fun ShippingMethod.toDto() = ShippingMethodDto(
    id = this.id.raw,
    name = this.methodName,
    defaultCost = this.defaultCost.toDto()
)

fun Cost.toDto() = ShippingMethodDto.CostDto(
    value = parseMoneyToString(this.value),
    currency = this.currency.toString()
)
