package pl.exbook.exbook.bookinfo.domain

data class BookInfo(
    val title: String,
    val subtitle: String?,
    val authors: List<String>,
    val publishedDate: String,
    val identifiers: List<BookIdentifier>
) {
    fun asSuggestion() = BookInfoSuggestion(
        title = this.title,
        subtitle = this.subtitle,
        author = this.authors.joinToString(separator = ", ")
    )
}

data class BookIdentifier(
    val type: String,
    val identifier: String
)
