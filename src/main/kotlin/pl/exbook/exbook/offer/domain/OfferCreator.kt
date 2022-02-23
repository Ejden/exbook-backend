package pl.exbook.exbook.offer.domain

import java.time.Instant
import java.util.UUID
import mu.KLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.exbook.exbook.offer.adapter.mongodb.OfferNotFoundException
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.OfferVersionId
import pl.exbook.exbook.stock.StockFacade
import pl.exbook.exbook.stock.domain.OfferChangeValidator
import pl.exbook.exbook.user.UserFacade

@Service
class OfferCreator(
    private val offerRepository: OfferRepository,
    private val userFacade: UserFacade,
    private val stockFacade: StockFacade,
    private val offerChangeValidator: OfferChangeValidator
) {
    @Transactional
    fun addOffer(request: CreateOfferCommand, token: UsernamePasswordAuthenticationToken): Offer {
        val user = userFacade.getUserByUsername(token.name)
        val offerId = OfferId(UUID.randomUUID().toString())
        val versionId = OfferVersionId(UUID.randomUUID().toString())
        val stock = stockFacade.createStockForOffer(offerId, request.initialStock)

        val offer = Offer(
            id = offerId,
            versionCreationDate = Instant.now(),
            versionExpireDate = null,
            versionId = versionId,
            book = Offer.Book(
                author = request.book.author,
                title = request.book.title,
                isbn = request.book.isbn,
                condition = request.book.condition
            ),
            images = Offer.Images(
                thumbnail = null,
                allImages = emptyList()
            ),
            description = request.description,
            type = request.type,
            seller = Offer.Seller(
                id = user.id
            ),
            price = request.price?.let { Money(it.amount, it.currency) },
            location = request.location,
            category = Offer.Category(request.category.id),
            shippingMethods = request.shippingMethods.map {
                Offer.ShippingMethod(
                    id = it.id,
                    price = it.price
                )
            },
            stockId = stock.id
        )

        val addedOffer = offerRepository.save(offer)

        logger.debug { "User with id = ${user.id.raw} created offer with id = ${offer.id.raw}" }

        return addedOffer
    }

    fun updateOffer(command: UpdateOfferCommand, currentOffer: Offer): Offer {
        offerChangeValidator.validateOfferChange(currentOffer, command)
        val newVersionId = OfferVersionId(UUID.randomUUID().toString())
        val newVersionValidFrom = Instant.now()
        val newOfferVersion = currentOffer.copy(
            versionId = newVersionId,
            versionCreationDate = newVersionValidFrom,
            versionExpireDate = null,
            book = Offer.Book(
                author = command.book.author,
                title = command.book.title,
                isbn = command.book.isbn,
                condition = command.book.condition
            ),
            images = Offer.Images(
                thumbnail = command.images.thumbnail?.let { Offer.Image(it.url) },
                allImages = command.images.allImages.map { Offer.Image(it.url) }
            ),
            description = command.description,
            type = command.type,
            price = command.price,
            location = command.location,
            shippingMethods = command.shippingMethods.map { Offer.ShippingMethod(it.id, it.price) }
        )

        return offerRepository.save(newOfferVersion)
    }

    companion object : KLogging()
}
