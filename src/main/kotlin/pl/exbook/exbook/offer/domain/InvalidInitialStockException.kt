package pl.exbook.exbook.offer.domain

class InvalidInitialStockException(amount: Int): RuntimeException("Invalid initial stock: $amount while creating offer")
