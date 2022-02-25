package pl.exbook.exbook.adapters

import pl.exbook.exbook.basket.adapter.mongodb.BasketNotFoundException
import pl.exbook.exbook.basket.domain.Basket
import pl.exbook.exbook.basket.domain.BasketRepository
import pl.exbook.exbook.shared.UserId

class InMemoryBasketRepository : BasketRepository {
    private val memory = mutableMapOf<UserId, Basket>()

    override fun getUserBasket(userId: UserId): Basket = memory[userId] ?: throw BasketNotFoundException()

    override fun save(basket: Basket): Basket {
        memory[basket.userId] = basket
        return memory[basket.userId]!!
    }

    fun removeBasket(userId: UserId) {
        memory.remove(userId)
    }
}
