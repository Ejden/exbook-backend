package pl.exbook.exbook.stock.domain

import pl.exbook.exbook.shared.OfferId
import pl.exbook.exbook.shared.StockId

class StockNotFoundException(stockId: StockId) : RuntimeException("Stock ${stockId.raw} not found")

class StockForOfferNotFoundException(offerId: OfferId) : RuntimeException("Stock for offer ${offerId.raw} not found")
