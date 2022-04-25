package pl.exbook.exbook.stock.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.shared.ValidationException

@Service
class StockValidator {
    fun validateCreationOfStock(createdQuantity: Long) {
        if (createdQuantity < 0) {
            throw StockValidationException("Cannot create stock with negative value = $createdQuantity")
        }
    }
}

class StockValidationException(message: String) : ValidationException(message)
