package pl.exbook.exbook.bookinfo

import org.springframework.stereotype.Service
import pl.exbook.exbook.bookinfo.domain.BookInfo
import pl.exbook.exbook.bookinfo.domain.BookInfoProvider
import pl.exbook.exbook.bookinfo.domain.BookInfoSuggestion

@Service
class BookInfoFacade(private val bookInfoProvider: BookInfoProvider) {
    fun getBookInfo(isbn: String): BookInfo = bookInfoProvider.getBookInformation(isbn)

    fun getBookSuggestion(isbn: String): BookInfoSuggestion = getBookInfo(isbn).asSuggestion()
}
