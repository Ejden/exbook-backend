package pl.exbook.exbook

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.adapter.mongodb.CategoryRepository
import pl.exbook.exbook.image.ImageFacade
import pl.exbook.exbook.image.adapter.mongodb.ImageRepository
import pl.exbook.exbook.listing.ListingFacade
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.adapter.mongodb.OfferRepository
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.adapter.mongodb.ShippingRepository
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.adapter.mongodb.UserRepository

@Configuration
class ServicesConfiguration {

    @Bean
    fun categoryFacade(categoryRepository: CategoryRepository) = CategoryFacade(categoryRepository)

    @Bean
    fun imageFacade(imageRepository: ImageRepository) = ImageFacade(imageRepository)

    @Bean
    fun userFacade(userRepository: UserRepository) = UserFacade(userRepository)

    @Bean
    fun offerFacade(offerRepository: OfferRepository, userFacade: UserFacade) = OfferFacade(offerRepository, userFacade)

    @Bean
    fun shippingFacade(shippingRepository: ShippingRepository) = ShippingFacade(shippingRepository)

    @Bean
    fun listingFacade(
        offerFacade: OfferFacade,
        userFacade: UserFacade,
        shippingFacade: ShippingFacade
    ) = ListingFacade(offerFacade, userFacade, shippingFacade)
}
