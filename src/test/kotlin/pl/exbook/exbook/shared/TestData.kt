package pl.exbook.exbook.shared

import java.math.BigDecimal
import org.bson.BsonBinarySubType
import org.bson.types.Binary
import org.springframework.mock.web.MockMultipartFile

object TestData {
    val sampleUserId = UserId("user-id")
    val sampleSellerId = UserId("seller-id")
    val otherSampleSellerId = UserId("other-seller-id")
    val sampleBuyerId = UserId("buyer-id")
    val otherSampleBuyerId = UserId("other-buyer-id")
    val sampleAdminId = UserId("admin-id")
    val sampleOfferId = OfferId("offer-id")
    val otherSampleOfferId = OfferId("other-offer-id")
    val thirdSampleOfferId = OfferId("third-offer-id")
    val sampleStockId = StockId("stock-id")
    val otherStockId = StockId("other-stock-id")
    val sampleStockReservationId = StockReservationId("stock-reservation-id")
    val sampleOfferVersionId = OfferVersionId("offer-version-id")
    val sampleCategoryId = CategoryId("category-id")
    val sampleShippingMethodId = ShippingMethodId("shipping-method-id")
    val otherSampleShippingMethodId = ShippingMethodId("other-shipping-method-id")
    val tenPln = Money(BigDecimal("10.00"), Currency.PLN)
    val sampleImageBinary = Binary(BsonBinarySubType.BINARY, byteArrayOf(0x1, 0x2, 0x3, 0x4, 0x5, 0xf))
    val sampleFile = MockMultipartFile("image", "image", "image/png", sampleImageBinary.data)
    val sampleImageId = ImageId("image-id")

    const val sampleCategoryName = "category-name"
    const val otherSampleCategoryName = "other-category-name"
    const val sampleBuyerUsername = "buyer-username"
    const val sampleSellerUsername = "seller-username"
    const val otherSampleSellerUsername = "other-seller-username"
    const val sampleAdminUsername = "admin-username"
    const val sampleSellerFirstName = "seller-first-name"
    const val sampleSellerLastName = "seller-last-name"
    const val samplePassword = "password"
    const val sampleSellerPassword = "seller-password"
    const val sampleSellerEmail = "seller-email@gmail.com"
    const val otherSampleSellerEmail = "other-seller@gmail.com"
    const val sampleAdminEmail = "admin@gmail.com"
    const val sampleShippingMethodName = "shipping-method"
    const val sampleImageUrl = "http://localhost:8082/api/images/d327ft3278fg23f7g32c23"
    const val otherSampleImageUrl = "http://localhost:8082/api/images/f4343g34g45g45gs"
}
