package pl.exbook.exbook.offer.domain

import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.shared.OfferId

class OfferNotFoundException(offerId: OfferId) : NotFoundException("Offer with id ${offerId.raw} not found")
