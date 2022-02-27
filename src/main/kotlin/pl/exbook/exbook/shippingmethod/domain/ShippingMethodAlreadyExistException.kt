package pl.exbook.exbook.shippingmethod.domain

import pl.exbook.exbook.shared.ValidationException

class ShippingMethodAlreadyExistException(name: String) : ValidationException("Shipping method with name $name already exist")
