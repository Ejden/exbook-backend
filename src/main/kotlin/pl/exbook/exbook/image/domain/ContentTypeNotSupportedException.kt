package pl.exbook.exbook.image.domain

import pl.exbook.exbook.shared.ApplicationException

data class ContentTypeNotSupportedException(val msg: String) : ApplicationException(msg)
