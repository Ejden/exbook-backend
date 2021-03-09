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

    fun addOffer(request: NewBookRequest, token: UsernamePasswordAuthenticationToken) : Offer {
        // Getting user from database that sent
        val user : User? = userService.findUserByUsername(token.name)

        val offer =  offerRepository.save(
            OfferDatabaseModel(
                id = null,
                book = Book(request.author, request.title, request.ISBN, request.condition),
                images = Images(),
                description = request.description,
                sellerId = user?.id!!
            )
        ).toOffer()

        logger.debug("User with id = ${user.id} added new offer with id = ${offer.id}")

        return offer
    }
}


