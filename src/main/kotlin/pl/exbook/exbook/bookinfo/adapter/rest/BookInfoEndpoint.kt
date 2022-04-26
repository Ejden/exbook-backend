package pl.exbook.exbook.bookinfo.adapter.rest

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.bookinfo.BookInfoFacade
import pl.exbook.exbook.bookinfo.adapter.rest.dto.BookInfoSuggestionDto
import pl.exbook.exbook.bookinfo.adapter.rest.dto.BookInfoSuggestionMapper
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.util.callhandler.handleRequest

@RestController
@RequestMapping("api/books")
class BookInfoEndpoint(private val bookInfoFacade: BookInfoFacade) {
    @GetMapping(produces = [ContentType.V1])
    fun getBookSuggestionByIsbn(@RequestParam isbn: String): ResponseEntity<BookInfoSuggestionDto> = handleRequest(
        mapper = BookInfoSuggestionMapper,
        call = { bookInfoFacade.getBookSuggestion(isbn) },
        response = { ok(it) }
    )
}
