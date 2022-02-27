package pl.exbook.exbook.offer.adapter.rest

import mu.KLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.adapter.rest.dto.CreateOfferRequest
import pl.exbook.exbook.offer.adapter.rest.dto.OfferDto
import pl.exbook.exbook.offer.adapter.rest.dto.UpdateOfferRequest
import pl.exbook.exbook.offer.domain.CreateOfferCommand
import pl.exbook.exbook.offer.domain.Offer
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.user.domain.UserNotFoundException

@RestController
@RequestMapping("api/offers")
class OfferEndpoint(private val offerFacade: OfferFacade) {
    @PostMapping(consumes = [ContentType.V1], produces = [ContentType.V1])
    @PreAuthorize("hasAuthority('EXCHANGE_BOOKS')")
    fun addOffer(@RequestBody request: CreateOfferRequest, user: UsernamePasswordAuthenticationToken?): ResponseEntity<OfferDto> {
        return if (user != null) {
            ResponseEntity.ok(offerFacade.addOffer(request.toCommand(), user).toDto())
        } else {
            logger.warn { "Non logged user tried to add new offer" }
            throw UserNotFoundException("Non logged user tried to add new offer")
        }
    }

    @PutMapping("{offerId}", consumes = [ContentType.V1], produces = [ContentType.V1])
    @PreAuthorize("hasAuthority('EXCHANGE_BOOKS')")
    fun updateOffer(
        @RequestBody request: UpdateOfferRequest,
        @PathVariable offerId: OfferId,
        token: UsernamePasswordAuthenticationToken?
    ): ResponseEntity<OfferDto> {
        return if (token != null) {
            ResponseEntity.ok(offerFacade.updateOffer(request.toCommand(offerId), token).toDto())
        } else {
            logger.warn { "Non logged user tried to add new offer" }
            throw UserNotFoundException("Non logged user tried to update offer")
        }
    }

    @GetMapping("{offerId}", produces = [ContentType.V1])
    fun getOffer(@PathVariable offerId: OfferId): OfferDto {
        return offerFacade.getOffer(offerId).toDto()
    }

    companion object : KLogging()
}

private fun CreateOfferRequest.toCommand() = CreateOfferCommand.fromRequest(this)
private fun Offer.toDto() = OfferDto.fromDomain(this)
