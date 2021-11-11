package pl.exbook.exbook.basket.domain

import java.util.UUID
import pl.exbook.exbook.shared.BasketId
import pl.exbook.exbook.shared.Currency
import pl.exbook.exbook.shared.Money
import pl.exbook.exbook.shared.UserId

class BasketFactory {

    fun createEmptyBasket(userId: UserId): Basket {
        return Basket(
            id = BasketId(UUID.randomUUID().toString()),
            userId = userId,
            items = mutableListOf()
        )
    }
}
