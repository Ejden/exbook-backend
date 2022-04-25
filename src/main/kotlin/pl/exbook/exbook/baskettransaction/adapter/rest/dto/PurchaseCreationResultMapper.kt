package pl.exbook.exbook.baskettransaction.adapter.rest.dto

import pl.exbook.exbook.baskettransaction.domain.PurchaseCreationResult
import pl.exbook.exbook.baskettransaction.domain.SuccessfulPurchaseCreationResult
import pl.exbook.exbook.baskettransaction.domain.UnsuccessfulPurchaseCreationResult
import pl.exbook.exbook.util.mapper.FromDomainMapper

object PurchaseCreationResultMapper : FromDomainMapper<PurchaseCreationResult, PurchaseCreationResultDto> {
    override fun fromDomain(from: PurchaseCreationResult): PurchaseCreationResultDto = when (from) {
        is SuccessfulPurchaseCreationResult -> SuccessfulPurchaseCreationResultDto(
            result = from.result.name,
            createdOrders = from.createdOrders.map { SuccessfulPurchaseCreationResultDto.Order(it.raw) }
        )
        is UnsuccessfulPurchaseCreationResult -> UnsuccessfulPurchaseCreationResultDto(
            result = from.result.name,
            reason = from.reason.name
        )
        else -> throw IllegalStateException("")
    }
}
