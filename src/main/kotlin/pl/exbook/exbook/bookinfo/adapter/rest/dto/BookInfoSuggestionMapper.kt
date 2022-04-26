package pl.exbook.exbook.bookinfo.adapter.rest.dto

import pl.exbook.exbook.bookinfo.domain.BookInfoSuggestion
import pl.exbook.exbook.util.mapper.FromDomainMapper

object BookInfoSuggestionMapper : FromDomainMapper<BookInfoSuggestion, BookInfoSuggestionDto> {
    override fun fromDomain(from: BookInfoSuggestion): BookInfoSuggestionDto = BookInfoSuggestionDto(
        title = from.title,
        subtitle = from.subtitle,
        author = from.author
    )
}
