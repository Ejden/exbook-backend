package pl.exbook.exbook.shared

open class ApplicationException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

open class IllegalParameterException(message: String? = null, cause: Throwable? = null) : ApplicationException(message, cause)

open class NotFoundException(message: String? = null, cause: Throwable? = null) : ApplicationException(message, cause)

open class ValidationException(message: String? = null, cause: Throwable? = null) : ApplicationException(message, cause)
