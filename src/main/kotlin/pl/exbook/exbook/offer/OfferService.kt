package pl.exbook.exbook.offer

import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import pl.exbook.exbook.user.User
import pl.exbook.exbook.user.UserService
import java.util.stream.Collectors

private val logger = KotlinLogging.logger {}

@Service
class OfferService (
    private val offerRepository: OfferRepository,
    private val userService: UserService
){

    fun getAllOffers() : MutableList<Offer> {
        return offerRepository.findAll()
            .stream()
            .map(OfferDatabaseModel::toOffer)
            .collect(Collectors.toList())
    }

    fun addOffer(request: NewOfferRequest, token: UsernamePasswordAuthenticationToken) : Offer? {
        // Getting user from database that sent
        val user : User? = userService.findUserByUsername(token.name)
        if (request.price != null) {
            if (request.price < 0) {
                return null
            }
        }

        val offer =  offerRepository.save(
            OfferDatabaseModel(
                id = null,
                book = request.book,
                images = request.images,
                description = request.description,
                sellerId = user?.id!!,
                type = request.type,
                price = request.price,
                location = request.location,
                categories = request.categories,
                shippingMethods = request.shippingMethods
            )
        ).toOffer()

        logger.debug("User with id = ${user.id} added new offer with id = ${offer.id}")

        return offer
    }
}


