package pl.exbook.exbook.shippingmethod.domain

import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.shared.ShippingMethodId

class ShippingMethodNotFoundException(
    id: ShippingMethodId
): NotFoundException("Shipping method with id ${id.raw} not found")
