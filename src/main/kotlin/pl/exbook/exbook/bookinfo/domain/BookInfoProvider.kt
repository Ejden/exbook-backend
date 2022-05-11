package pl.exbook.exbook.bookinfo.domain

interface BookInfoProvider {
    fun getBookInformation(isbn: String): BookInfo
}
