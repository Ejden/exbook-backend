package pl.exbook.exbook.stock.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.ValidationException

@Service
class StockValidator {
    fun validateCreationOfStock(offerId: OfferId, createdQuantity: Int) {
        if (createdQuantity < 0) {
            throw StockValidationException("Cannot create stock for ${offerId.raw} with negative value = $createdQuantity")
        }
    }
}

class StockValidationException(message: String) : ValidationException(message)
