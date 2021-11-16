package pl.exbook.exbook.shippingmethod.adapter.rest

import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.domain.Cost
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod

const val CONTENT_TYPE = "application/vnd.exbook.v1+json"

@RestController
@RequestMapping("api/shipping")
class ShippingEndpoint(
    val shippingMethodFacade: ShippingMethodFacade
) {

    @GetMapping(produces = [CONTENT_TYPE])
    fun getShippingMethods(): List<ShippingMethodDto> {
        return shippingMethodFacade.getShippingMethods().map { it.toDto() }
    }

    @PostMapping(produces = [CONTENT_TYPE])
    fun addShippingMethod(@RequestBody requestBody: NewShippingMethod): ShippingMethodDto {
        return shippingMethodFacade.addShippingMethod(requestBody).toDto()
    }
}

data class NewShippingMethod(
    val name: String,
    val pickupPointMethod: Boolean,
    val cost: Cost
) {
    data class Cost(
        val defaultCost: String,
        val canBeOverridden: Boolean
    )
}

data class ShippingMethodDto(
    val id: String,
    val name: String,
    val pickupPointMethod: Boolean,
    val defaultCost: CostDto
) {
    data class CostDto(
        val amount: String,
        val currency: String,
        val canBeOverridden: Boolean
    )
}

fun ShippingMethod.toDto() = ShippingMethodDto(
    id = this.id.raw,
    name = this.methodName,
    pickupPointMethod = this.pickupPointMethod,
    defaultCost = this.defaultCost.toDto()
)

fun Cost.toDto() = ShippingMethodDto.CostDto(
    amount = this.amount.toString(),
    currency = this.currency.toString(),
    canBeOverridden = this.canBeOverridden
)
