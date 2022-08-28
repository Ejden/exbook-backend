package pl.exbook.exbook.shared

open class ApplicationException(
    message: String? = null,
    cause: Throwable? = null,
    val userMessage: String? = null,
    val userMessageKey: String? = null
) : RuntimeException(message, cause)

open class IllegalParameterException(
    message: String? = null,
    userMessageKey: String? = null,
    cause: Throwable? = null,
) : ApplicationException(
    message = message,
    cause = cause,
    userMessageKey = userMessageKey
)

open class NotFoundException(
    message: String? = null,
    cause: Throwable? = null,
    userMessage: String? = null,
) : ApplicationException(message, cause, userMessage)

open class ValidationException(
    message: String? = null,
    userMessageKey: String = "validation.error.commonErrorMsg",
    cause: Throwable? = null
) : ApplicationException(
    message = message,
    userMessageKey = userMessageKey,
    cause = cause
)

open class ExternalServiceException(message: String?, cause: Throwable? = null) : ApplicationException(message, cause)
