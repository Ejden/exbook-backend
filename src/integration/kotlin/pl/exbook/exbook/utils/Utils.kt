package pl.exbook.exbook.utils

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import pl.exbook.exbook.shared.dto.MoneyDto
import java.math.BigDecimal

fun <T> createHttpEntity(
    body: T? = null,
    withAcceptHeader: Boolean = false,
    withContentTypeHeader: Boolean = false,
    token: String? = null
): HttpEntity<T> {
    val headers = HttpHeaders()
    if (withAcceptHeader) {
        headers.add("Accept", "application/vnd.exbook.v1+json")
    }
    if (withContentTypeHeader) {
        headers.add("Content-Type", "application/vnd.exbook.v1+json")
    }
    if(token != null) {
        headers.add("Cookie", "Authorization=$token")
    }

    return HttpEntity(body, headers)
}

fun String.plnDto() = MoneyDto(BigDecimal(this), "PLN")
