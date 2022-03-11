package pl.exbook.exbook.offer.domain

import java.time.Instant
import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId

class OfferVersionNotFoundException private constructor(msg: String? = null, order: Int) : NotFoundException(msg) {
    constructor(offerId: OfferId) : this("Couldn't find active offer ${offerId.raw} version", 1)
    constructor(offerId: OfferId, version: Instant) : this("Couldn't find offer ${offerId.raw} version from $version", 2)
    constructor(offerVersionId: OfferVersionId) : this("Couldn't find active offer version ${offerVersionId.raw}", 3)
}
