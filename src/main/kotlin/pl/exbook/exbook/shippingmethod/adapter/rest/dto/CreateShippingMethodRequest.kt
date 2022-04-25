package pl.exbook.exbook.shippingmethod.adapter.rest.dto

import javax.validation.constraints.NotEmpty
import pl.exbook.exbook.shared.dto.MoneyDto
import pl.exbook.exbook.shared.dto.toDomain
import pl.exbook.exbook.shippingmethod.domain.NewShippingMethodCommand
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType

data class CreateShippingMethodRequest(
    @field:NotEmpty
    val name: String,
    val shippingMethodType: String,
    val cost: Cost
) {
    data class Cost(
        val defaultCost: MoneyDto,
        val canBeOverridden: Boolean
    )

    fun toCommand() = NewShippingMethodCommand(
        name = this.name,
        shippingMethodType = ShippingMethodType.valueOf(this.shippingMethodType),
        cost = this.cost.toCommand()
    )

    private fun Cost.toCommand() = NewShippingMethodCommand.Cost(
        defaultCost = this.defaultCost.toDomain(),
        canBeOverridden = canBeOverridden
    )
}
