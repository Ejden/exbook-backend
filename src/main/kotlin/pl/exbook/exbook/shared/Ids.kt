package pl.exbook.exbook.shared

data class OfferId(val raw: String)

data class ShippingId(val raw: String)

data class ShippingMethodId(val raw: String)

data class PickupPointId(val raw: String)

data class UserId(val raw: String)

data class CategoryId(val raw: String)

data class ImageId(val raw: String)

data class UserStatisticsId(val raw: String)

data class OrderId(val raw: String)

@JvmInline
value class BasketId(val raw: String)

@JvmInline
value class StockId(val raw: String)

@JvmInline
value class StockReservationId(val raw: String)

@JvmInline
value class EventId(val raw: String)

@JvmInline
value class PurchaseId(val raw: String)

@JvmInline
value class OfferVersionId(val raw: String)

@JvmInline
value class ExchangeBookId(val raw: String)
