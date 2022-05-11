package pl.exbook.exbook

import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import pl.exbook.exbook.image.domain.ContentTypeNotSupportedException
import pl.exbook.exbook.security.domain.UnauthorizedException
import pl.exbook.exbook.shared.ExternalServiceException
import pl.exbook.exbook.shared.IllegalParameterException
import pl.exbook.exbook.shared.NotFoundException
import pl.exbook.exbook.shared.ValidationException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(TokenExpiredException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handle(cause: TokenExpiredException) {}

    @ExceptionHandler(UnauthorizedException::class)
    fun handle(cause: UnauthorizedException): ResponseEntity<ErrorResponse> {
        logger.warn(cause.message)

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = UnauthorizedException::class.simpleName
                )
            )
    }

    @ExceptionHandler(NotFoundException::class)
    fun handle(cause: NotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn(cause.message)

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = NotFoundException::class.simpleName,
                    userMessage = cause.userMessage
                )
            )
    }

    @ExceptionHandler(ContentTypeNotSupportedException::class)
    fun handle(cause: ContentTypeNotSupportedException): ResponseEntity<ErrorResponse> {
        logger.warn(cause.message)

        return ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = NotFoundException::class.simpleName,
                    userMessage = cause.userMessage
                )
            )
    }

    @ExceptionHandler(MismatchedInputException::class)
    fun handle(cause: MismatchedInputException): ResponseEntity<ErrorResponse> {
        logger.warn(cause.message)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = MismatchedInputException::class.simpleName,
                )
            )
    }

    @ExceptionHandler(ValidationException::class)
    fun handle(cause: ValidationException): ResponseEntity<ErrorResponse> {
        logger.warn(cause.message)

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = ValidationException::class.simpleName
                )
            )
    }

    @ExceptionHandler(IllegalParameterException::class)
    fun handle(cause: IllegalParameterException): ResponseEntity<ErrorResponse> {
        logger.warn(cause.message)

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = IllegalParameterException::class.simpleName
                )
            )
    }

    @ExceptionHandler(ExternalServiceException::class)
    fun handle(cause: ExternalServiceException): ResponseEntity<ErrorResponse> {
        logger.warn { cause.message }

        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(
                ErrorResponse(
                    message = cause.message.toString(),
                    code = ExternalServiceException::class.simpleName
                )
            )
    }

    companion object : KLogging()
}

data class ErrorResponse(
    val message: String,
    val code: String? = null,
    val details: String? = null,
    val path: String? = null,
    val userMessage: String? = null
)
