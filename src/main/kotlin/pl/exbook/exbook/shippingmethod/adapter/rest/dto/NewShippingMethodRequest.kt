package pl.exbook.exbook.shippingmethod.adapter.rest.dto

import javax.validation.constraints.NotEmpty
import pl.exbook.exbook.shared.dto.MoneyDocument
import pl.exbook.exbook.shippingmethod.domain.NewShippingMethodCommand

data class NewShippingMethodRequest(
    @field:NotEmpty
    val name: String,
    val pickupPointMethod: Boolean,
    val cost: Cost
) {
    data class Cost(
        val defaultCost: MoneyDocument,
        val canBeOverridden: Boolean
    )

    fun toCommand() = NewShippingMethodCommand(
        name = this.name,
        pickupPointMethod = this.pickupPointMethod,
        cost = this.cost.toCommand()
    )

    private fun Cost.toCommand() = NewShippingMethodCommand.Cost(
        defaultCost = this.defaultCost.toDomain(),
        canBeOverridden = canBeOverridden
    )
}
