package pl.exbook.exbook.shared

import java.math.BigDecimal

object TestData {
    val sampleUserId = UserId("user-id")
    val sampleSellerId = UserId("seller-id")
    val otherSampleSellerId = UserId("other-seller-id")
    val sampleBuyerId = UserId("buyer-id")
    val otherSampleBuyerId = UserId("other-buyer-id")
    val sampleOfferId = OfferId("offer-id")
    val otherSampleOfferId = OfferId("other-offer-id")
    val thirdSampleOfferId = OfferId("third-offer-id")
    val sampleStockId = StockId("stock-id")
    val otherStockId = StockId("other-stock-id")
    val sampleStockReservationId = StockReservationId("stock-reservation-id")
    val sampleOfferVersionId = OfferVersionId("offer-version-id")
    val sampleCategoryId = CategoryId("category-id")
    val sampleShippingMethodId = ShippingMethodId("shipping-method-id")
    val tenPln = Money(BigDecimal("10.00"), Currency.PLN)
    const val sampleBuyerUsername = "buyer-username"
    const val sampleSellerUsername = "seller-username"
    const val otherSampleSellerUsername = "other-seller-username"
    const val sampleImageUrl = "http://localhost:8082/api/images/d327ft3278fg23f7g32c23"
    const val otherSampleImageUrl = "http://localhost:8082/api/images/f4343g34g45g45gs"
    const val USER_ID = "user-id"
    const val CATEGORY_ID_1 = "1"
    const val CATEGORY_ID_2 = "2"
    const val CATEGORY_ID_3 = "3"
    const val CATEGORY_NAME_1 = "category-1"
    const val CATEGORY_NAME_2 = "category-2"
    const val CATEGORY_NAME_3 = "category-3"
    const val IMAGE_URL_1 = "https://files.exbook.com/images/1"
    const val IMAGE_URL_2 = "https://files.exbook.com/images/2"
    const val IMAGE_URL_3 = "https://files.exbook.com/images/3"
}
