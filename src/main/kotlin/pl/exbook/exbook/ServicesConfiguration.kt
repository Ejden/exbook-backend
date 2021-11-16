package pl.exbook.exbook

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.exbook.exbook.basket.BasketFacade
import pl.exbook.exbook.basket.adapter.mongodb.DatabaseBasketRepository
import pl.exbook.exbook.basket.adapter.mongodb.MongoBasketRepository
import pl.exbook.exbook.basket.domain.BasketFactory
import pl.exbook.exbook.basket.domain.BasketRepository
import pl.exbook.exbook.basket.domain.BasketValidator
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
import pl.exbook.exbook.order.domain.OrderFactory
import pl.exbook.exbook.order.domain.OrderValidator
import pl.exbook.exbook.shipping.ShippingFacade
import pl.exbook.exbook.shipping.adapter.mongodb.DatabaseShippingRepository
import pl.exbook.exbook.shipping.adapter.mongodb.MongoShippingRepository
import pl.exbook.exbook.shipping.domain.ShippingCalculator
import pl.exbook.exbook.shipping.domain.ShippingFactory
import pl.exbook.exbook.shipping.domain.ShippingRepository
import pl.exbook.exbook.shipping.domain.ShippingValidator
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.adapter.mongodb.DatabaseShippingMethodRepository
import pl.exbook.exbook.shippingmethod.adapter.mongodb.MongoShippingMethodRepository
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodRepository
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
    fun shippingMethodFacade(shippingRepository: ShippingMethodRepository) = ShippingMethodFacade(shippingRepository)

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
    fun orderFactory(offerFacade: OfferFacade) = OrderFactory(offerFacade)

    @Bean
    fun shippingValidator() = ShippingValidator()

    @Bean
    fun shippingFactory() = ShippingFactory()

    @Bean
    fun shippingCalculator(
        offerFacade: OfferFacade,
        shippingFactory: ShippingFactory
    ) = ShippingCalculator(offerFacade, shippingFactory)

    @Bean
    fun shippingRepository(
        mongoShippingRepository: MongoShippingRepository
    ) = DatabaseShippingRepository(mongoShippingRepository)

    @Bean
    fun shippingFacade(
        shippingMethodFacade: ShippingMethodFacade,
        shippingValidator: ShippingValidator,
        shippingCalculator: ShippingCalculator,
        shippingRepository: ShippingRepository
    ) = ShippingFacade(shippingMethodFacade, shippingValidator, shippingCalculator, shippingRepository)

    @Bean
    fun orderValidator() = OrderValidator()

    @Bean
    fun basketRepository(mongoBasketRepository: MongoBasketRepository) = DatabaseBasketRepository(mongoBasketRepository)

    @Bean
    fun basketValidator() = BasketValidator()

    @Bean
    fun basketFactory() = BasketFactory()

    @Bean
    fun basketFacade(
        basketRepository: BasketRepository,
        userFacade: UserFacade,
        offerFacade: OfferFacade,
        basketValidator: BasketValidator,
        basketFactory: BasketFactory
    ) = BasketFacade(basketRepository, userFacade, offerFacade, basketValidator, basketFactory)

    @Bean
    fun orderFacade(
        orderRepository: DatabaseOrderRepository,
        userFacade: UserFacade,
        shippingFacade: ShippingFacade,
        offerFacade: OfferFacade,
        orderValidator: OrderValidator,
        orderFactory: OrderFactory,
    ) = OrderFacade(orderRepository, userFacade, shippingFacade, offerFacade, orderValidator, orderFactory)
}
