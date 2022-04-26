package pl.exbook.exbook.baskettransaction.adapter.rest

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.baskettransaction.BasketTransactionFacade
import pl.exbook.exbook.baskettransaction.adapter.rest.dto.DetailedDraftPurchaseDto
import pl.exbook.exbook.baskettransaction.adapter.rest.dto.PreviewPurchaseMapper
import pl.exbook.exbook.baskettransaction.adapter.rest.dto.PreviewPurchaseRequest
import pl.exbook.exbook.baskettransaction.adapter.rest.dto.PurchaseCreationResultDto
import pl.exbook.exbook.baskettransaction.adapter.rest.dto.PurchaseCreationResultMapper
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.util.callhandler.handleRequest

@RestController
@RequestMapping("api/purchase")
class DraftPurchaseEndpoint(private val basketTransactionFacade: BasketTransactionFacade) {
    @PutMapping("preview", consumes = [ContentType.V1], produces = [ContentType.V1])
    @PreAuthorize("isFullyAuthenticated()")
    fun previewTransaction(
        token: UsernamePasswordAuthenticationToken,
        @RequestBody request: PreviewPurchaseRequest
    ): ResponseEntity<DetailedDraftPurchaseDto> = handleRequest(
        mapper = PreviewPurchaseMapper,
        requestBody = request,
        call = { basketTransactionFacade.previewPurchase(token.name, it) },
        response = { ok(it) }
    )

    @PostMapping("realise", produces = [ContentType.V1])
    @PreAuthorize("isFullyAuthenticated()")
    fun realisePurchase(
        token: UsernamePasswordAuthenticationToken
    ): ResponseEntity<PurchaseCreationResultDto> = handleRequest(
        mapper = PurchaseCreationResultMapper,
        call = { basketTransactionFacade.realisePurchase(token.name) },
        response = { ok(it) }
    )
}
