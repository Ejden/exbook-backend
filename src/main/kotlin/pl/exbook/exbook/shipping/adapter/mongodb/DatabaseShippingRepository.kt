package pl.exbook.exbook.shipping.adapter.mongodb

import pl.exbook.exbook.shared.PickupPointId
import pl.exbook.exbook.shared.ShippingId
import pl.exbook.exbook.shared.ShippingMethodId
import pl.exbook.exbook.shared.dto.toDocument
import pl.exbook.exbook.shipping.domain.AddressShipping
import pl.exbook.exbook.shipping.domain.PickupPointShipping
import pl.exbook.exbook.shipping.domain.Shipping
import pl.exbook.exbook.shipping.domain.ShippingRepository
import java.lang.RuntimeException

class DatabaseShippingRepository(
    private val mongoShippingRepository: MongoShippingRepository
) : ShippingRepository {

    override fun findById(shippingId: ShippingId): Shipping {
        return mongoShippingRepository
            .findById(shippingId.raw)
            .orElseThrow{ ShippingNotFoundException(shippingId) }
            .toDomain()
    }

    override fun save(shipping: Shipping): Shipping {
        return mongoShippingRepository.save(shipping.toDocument()).toDomain()
    }

    override fun remove(shippingId: ShippingId) = mongoShippingRepository.deleteById(shippingId.raw)
}

private fun ShippingDocument.toDomain(): Shipping {
    return when (this) {
        is PickupPointShippingDocument -> this.toPickupPointDomain()
        is AddressShippingDocument -> this.toAddressDomain()
        else -> throw TypeCastException()
    }
}

private fun PickupPointShippingDocument.toPickupPointDomain() = PickupPointShipping(
    id = ShippingId(this.id!!),
    shippingMethodId = ShippingMethodId(this.shippingMethodId),
    shippingMethodName = this.shippingMethodName,
    cost = Shipping.Cost(this.cost.finalCost.toDomain()),
    pickupPoint = this.pickupPoint.toDomain()
)

private fun AddressShippingDocument.toAddressDomain() = AddressShipping(
    id = ShippingId(this.id!!),
    shippingMethodId = ShippingMethodId(this.shippingMethodId),
    shippingMethodName = this.shippingMethodName,
    cost = Shipping.Cost(this.cost.finalCost.toDomain()),
    address = this.address.toDomain()
)

private fun AddressDocument.toDomain() = Shipping.ShippingAddress(
    firstAndLastName = this.firstAndLastName,
    phoneNumber = this.phoneNumber,
    email = this.email,
    address = this.address,
    postalCode = this.postalCode,
    city = this.city,
    country = this.country
)

private fun PickupPointDocument.toDomain() = Shipping.PickupPoint(
    firstAndLastName = this.firstAndLastName,
    phoneNumber = this.phoneNumber,
    email = this.email,
    pickupPointId = PickupPointId(this.pickupPointId)
)

private fun Shipping.toDocument(): ShippingDocument {
    return when (this) {
        is PickupPointShipping -> this.toDocument()
        is AddressShipping -> this.toDocument()
        else -> throw TypeCastException()
    }
}

private fun PickupPointShipping.toDocument() = PickupPointShippingDocument(
    id = this.id.raw,
    shippingMethodId = this.shippingMethodId.raw,
    shippingMethodName = this.shippingMethodName,
    cost = DeliveryCostDocument(this.cost.finalCost.toDocument()),
    pickupPoint = this.pickupPoint.toDocument()
)

private fun AddressShipping.toDocument() = AddressShippingDocument(
    id = this.id.raw,
    shippingMethodId = this.shippingMethodId.raw,
    shippingMethodName = this.shippingMethodName,
    cost = DeliveryCostDocument(this.cost.finalCost.toDocument()),
    address = this.address.toDocument(),
)

private fun Shipping.ShippingAddress.toDocument() = AddressDocument(
    firstAndLastName = this.firstAndLastName,
    phoneNumber = this.phoneNumber,
    email = this.email,
    address = this.address,
    postalCode = this.postalCode,
    city = this.city,
    country = this.country
)

private fun Shipping.PickupPoint.toDocument() = PickupPointDocument(
    firstAndLastName = this.firstAndLastName,
    phoneNumber = this.phoneNumber,
    email = this.email,
    pickupPointId = this.pickupPointId.raw
)

class ShippingNotFoundException(shippingId: ShippingId) : RuntimeException("Shipping with id $shippingId not found")
