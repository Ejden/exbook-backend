package pl.exbook.exbook

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import pl.exbook.exbook.offer.OfferFacade
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
import pl.exbook.exbook.util.retrofit.RetrofitServiceRegistrar

@Configuration
@Import(RetrofitServiceRegistrar::class)
class ServicesConfiguration {
    @Bean
    fun orderRepository(mongoOrderRepository: MongoOrderRepository) = DatabaseOrderRepository(mongoOrderRepository)

    @Bean
    fun orderFactory(offerFacade: OfferFacade) = OrderFactory()

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
}
