package pl.exbook.exbook.baskettransaction.adapter.rest.dto

import org.springframework.context.MessageSource
import pl.exbook.exbook.baskettransaction.domain.PurchaseCreationResult
import pl.exbook.exbook.util.mapper.FromDomainMapper
import java.util.Locale

class PurchaseCreationResultMapper(
    private val messageSource: MessageSource,
    private val locale: Locale
) : FromDomainMapper<PurchaseCreationResult, PurchaseCreationResultDto> {
    override fun fromDomain(from: PurchaseCreationResult) = PurchaseCreationResultDto(
        result = from.result.name,
        numberOfCreatedOrders = from.numberOfCreatedOrders,
        numberOfFailedOrders = from.numberOfFailedOrders,
        createdOrders = from.createdOrders.map { it.raw },
        errorsByOrder = from.errorsByOrder.map {
            it.key.raw to OrderCreationError(
                code = it.value.name,
                userMessage = messageSource.getMessage(it.value.userMessageKey, null, locale)
            )
        }
            .toMap(),
        purchaseCreationError = from.purchaseCreationError?.let {
            PurchaseCreationError(
                code = it.name,
                userMessage = messageSource.getMessage(it.userMessageKey, null, locale)
            )
        }
    )
}
