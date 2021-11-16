package pl.exbook.exbook

import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import pl.exbook.exbook.image.ImageNotFoundException
import pl.exbook.exbook.offer.adapter.mongodb.OfferNotFoundException

@ControllerAdvice
class ExceptionHandler {

    companion object : KLogging()

    @ExceptionHandler(TokenExpiredException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handle(cause: TokenExpiredException) {}

    @ExceptionHandler(ImageNotFoundException::class)
    fun handle(cause: ImageNotFoundException): ResponseEntity<ErrorResponse> {
        logger.error(cause.message)

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = ImageNotFoundException::class.simpleName,
                    details = null,
                    path = "id",
                    userMessage = null
                )
            )
    }

    @ExceptionHandler(OfferNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handle(cause: OfferNotFoundException) {}

    @ExceptionHandler(MismatchedInputException::class)
    fun handle(cause: MismatchedInputException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = MismatchedInputException::class.simpleName,
                    details = null,
                    path = "",
                    userMessage = null
                )
            )
    }
}

data class ErrorResponse(
    val message: String,
    val code: String?,
    val details: String?,
    val path: String,
    val userMessage: String?
)
