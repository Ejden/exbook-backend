package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.StockId

class StockNotFoundException(stockId: StockId) : NotFoundException("Stock ${stockId.raw} not found")

class StockForOfferNotFoundException(offerId: OfferId) : NotFoundException("Stock for offer ${offerId.raw} not found")
