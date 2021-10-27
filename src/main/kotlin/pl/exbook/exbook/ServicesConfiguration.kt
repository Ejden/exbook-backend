package pl.exbook.exbook

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.exbook.exbook.category.CategoryFacade
import pl.exbook.exbook.category.adapter.mongodb.DatabaseCategoryRepository
import pl.exbook.exbook.category.adapter.mongodb.MongoCategoryRepository
import pl.exbook.exbook.category.domain.CategoryRepository
import pl.exbook.exbook.image.ImageFacade
import pl.exbook.exbook.image.adapter.mongodb.DatabaseImageRepository
import pl.exbook.exbook.image.adapter.mongodb.MongoImageRepository
import pl.exbook.exbook.image.domain.ImageRepository
import pl.exbook.exbook.listing.ListingFacade
import pl.exbook.exbook.offer.OfferFacade
import pl.exbook.exbook.offer.adapter.mongodb.DatabaseOfferRepository
import pl.exbook.exbook.offer.adapter.mongodb.MongoOfferRepository
import pl.exbook.exbook.offer.domain.OfferRepository
import pl.exbook.exbook.order.OrderFacade
import pl.exbook.exbook.order.adapter.mongodb.DatabaseOrderRepository
import pl.exbook.exbook.order.adapter.mongodb.MongoOrderRepository
import pl.exbook.exbook.shipping.ShippingMethodFacade
import pl.exbook.exbook.shipping.adapter.mongodb.DatabaseShippingMethodRepository
import pl.exbook.exbook.shipping.adapter.mongodb.MongoShippingMethodRepository
import pl.exbook.exbook.shipping.domain.ShippingMethodRepository
import pl.exbook.exbook.statistics.UserStatisticsFacade
import pl.exbook.exbook.statistics.adapter.mongodb.DatabaseUserStatisticsRepository
import pl.exbook.exbook.statistics.adapter.mongodb.MongoUserStatisticsRepository
import pl.exbook.exbook.statistics.domain.UserStatisticsRepository
import pl.exbook.exbook.user.UserFacade
import pl.exbook.exbook.user.adapter.mongodb.DatabaseUserRepository
import pl.exbook.exbook.user.adapter.mongodb.MongoUserRepository
import pl.exbook.exbook.user.domain.UserRepository

@Configuration
class ServicesConfiguration {

    @Bean
    fun categoryRepository(
        mongoCategoryRepository: MongoCategoryRepository
    ) = DatabaseCategoryRepository(mongoCategoryRepository)

    @Bean
    fun categoryFacade(categoryRepository: CategoryRepository) = CategoryFacade(categoryRepository)

    @Bean
    fun imageRepository(mongoImageRepository: MongoImageRepository) = DatabaseImageRepository(mongoImageRepository)

    @Bean
    fun imageFacade(imageRepository: ImageRepository) = ImageFacade(imageRepository)

    @Bean
    fun userRepository(mongoUserRepository: MongoUserRepository) = DatabaseUserRepository(mongoUserRepository)

    @Bean
    fun userFacade(userRepository: UserRepository) = UserFacade(userRepository)

    @Bean
    fun offerRepository(mongoOfferRepository: MongoOfferRepository) = DatabaseOfferRepository(mongoOfferRepository)

    @Bean
    fun offerFacade(offerRepository: OfferRepository, userFacade: UserFacade) = OfferFacade(offerRepository, userFacade)

    @Bean
    fun shippingMethodRepository(
        mongoShippingMethodRepository: MongoShippingMethodRepository
    ) = DatabaseShippingMethodRepository(mongoShippingMethodRepository)

    @Bean
    fun shippingFacade(shippingRepository: ShippingMethodRepository) = ShippingMethodFacade(shippingRepository)

    @Bean
    fun listingFacade(
        offerFacade: OfferFacade,
        userFacade: UserFacade,
        shippingMethodFacade: ShippingMethodFacade,
        applicationEventPublisher: ApplicationEventPublisher
    ) = ListingFacade(offerFacade, userFacade, shippingMethodFacade, applicationEventPublisher)

    @Bean
    fun userStatisticsRepository(
        mongoUserStatisticsRepository: MongoUserStatisticsRepository
    ) = DatabaseUserStatisticsRepository(mongoUserStatisticsRepository)

    @Bean
    fun statisticsFacade(
        userStatisticsRepository: UserStatisticsRepository
    ) = UserStatisticsFacade(userStatisticsRepository)

    @Bean
    fun orderRepository(mongoOrderRepository: MongoOrderRepository) = DatabaseOrderRepository(mongoOrderRepository)

    @Bean
    fun orderFacade(
        orderRepository: DatabaseOrderRepository,
        offerFacade: OfferFacade,
        userFacade: UserFacade
    ) = OrderFacade(orderRepository, offerFacade, userFacade)
}
