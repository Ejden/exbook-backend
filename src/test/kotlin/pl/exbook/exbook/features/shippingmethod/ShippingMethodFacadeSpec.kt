package pl.exbook.exbook.features.shippingmethod

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import pl.exbook.exbook.ability.ShippingMethodDomainAbility
import pl.exbook.exbook.pln
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.TestData.sampleShippingMethodId
import pl.exbook.exbook.shippingmethod.domain.NewShippingMethodCommand
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodAlreadyExistException
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodNotFoundException
import pl.exbook.exbook.shippingmethod.domain.ShippingMethodType

class ShippingMethodFacadeSpec : ShouldSpec({
    val domain = ShippingMethodDomainAbility()

    context("create new shipping method") {
        withData(
            "8.99".pln(),
            "0.00".pln()
        ) { defaultCost ->
            // given
            val command = NewShippingMethodCommand(
                name = "DPD",
                shippingMethodType = ShippingMethodType.ADDRESS_DELIVERY,
                cost = NewShippingMethodCommand.Cost(
                    defaultCost = defaultCost,
                    canBeOverridden = true
                )
            )

            // when
            val result = domain.facade.addShippingMethod(command)
            val shippingMethod = domain.facade.getShippingMethod(result.id)

            // then
            shippingMethod.shouldNotBeNull()
            shippingMethod.methodName shouldBe "DPD"
            shippingMethod.type shouldBe ShippingMethodType.ADDRESS_DELIVERY
            shippingMethod.defaultCost.cost shouldBeEqualComparingTo defaultCost
            shippingMethod.defaultCost.canBeOverridden shouldBe true
        }
    }

    should("throw an error when trying to add new shipping method with already existing shipping method name") {
        // given
        domain.thereIsShippingMethod(name = "DPD")

        // then
        shouldThrowExactly<ShippingMethodAlreadyExistException> {
            domain.facade.addShippingMethod(
                NewShippingMethodCommand(
                    name = "DPD",
                    shippingMethodType = ShippingMethodType.ADDRESS_DELIVERY,
                    cost = NewShippingMethodCommand.Cost(
                        defaultCost = "8.99".pln(),
                        canBeOverridden = true
                    )
                )
            )
        }
    }

    should("throw an error when trying to add new shipping method with empty name") {
        // then
        shouldThrowExactly<IllegalParameterException> {
            domain.facade.addShippingMethod(
                NewShippingMethodCommand(
                    name = "",
                    shippingMethodType = ShippingMethodType.ADDRESS_DELIVERY,
                    cost = NewShippingMethodCommand.Cost(
                        defaultCost = "8.99".pln(),
                        canBeOverridden = true
                    )
                )
            )
        }
    }

    should("throw an error when trying to add new shipping method with negative cost") {
        // then
        shouldThrowExactly<IllegalParameterException> {
            domain.facade.addShippingMethod(
                NewShippingMethodCommand(
                    name = "DPD",
                    shippingMethodType = ShippingMethodType.ADDRESS_DELIVERY,
                    cost = NewShippingMethodCommand.Cost(
                        defaultCost = "-0.01".pln(),
                        canBeOverridden = true
                    )
                )
            )
        }
    }

    should("should throw an error when trying to get non existing shipping method") {
        // then
        shouldThrowExactly<ShippingMethodNotFoundException> {
            domain.facade.getShippingMethodById(sampleShippingMethodId)
        }
    }

    should("return null when trying to get non existing shipping method") {
        // when
        val shippingMethod = domain.facade.getShippingMethod(sampleShippingMethodId)

        // then
        shippingMethod.shouldBeNull()
    }

    should("get all shipping methods") {
        // given
        domain.thereIsShippingMethod(name = "DPD")
        domain.thereIsShippingMethod(name = "InPost")
        domain.thereIsShippingMethod(name = "DHL")

        // when
        val shippingMethods = domain.facade.getShippingMethods()

        // then
        shippingMethods shouldHaveSize 3
    }
})
