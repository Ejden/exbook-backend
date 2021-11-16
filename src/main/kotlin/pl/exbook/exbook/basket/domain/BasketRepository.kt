package pl.exbook.exbook.basket.domain

import pl.exbook.exbook.shared.UserId

interface BasketRepository {

    fun getUserBasket(userId: UserId): Basket

    fun save(basket: Basket): Basket
}
