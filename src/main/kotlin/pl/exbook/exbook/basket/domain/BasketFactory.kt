package pl.exbook.exbook.basket.domain

import org.springframework.stereotype.Service
import java.util.UUID
import pl.exbook.exbook.shared.BasketId
import pl.exbook.exbook.shared.UserId

@Service
class BasketFactory {
    fun createEmptyBasket(userId: UserId): Basket {
        return Basket(
            id = BasketId(UUID.randomUUID().toString()),
            userId = userId,
            itemsGroups = mutableMapOf()
        )
    }
}
