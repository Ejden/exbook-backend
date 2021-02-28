package pl.exbook.exbook.book

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors

@RestController
@RequestMapping("api/v1/books")
@PreAuthorize("isAuthenticated()")
class BookController (private val bookService: BookService) {

    @GetMapping
    @PreAuthorize("hasAuthority('SEARCH_BOOKS')")
    fun getAllBooks() : MutableCollection<BookDto> {
        return bookService.getAllBooks()
            .stream()
            .map(Book::toBookDto)
            .collect(Collectors.toList())
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EXCHANGE_BOOKS')")
    fun addBook(@RequestBody book : NewBookRequest, user : UsernamePasswordAuthenticationToken?) : BookDto? {
        return if (user != null) {
            bookService.addBook(book, user).toBookDto()
        } else
            null
    }
}

data class NewBookRequest(
    val author: String,
    val title: String,
    val ISBN: String?,
    val description: String?,
    val condition: Condition
) {

}

data class BookDto(
    val id: String,
    val author: String,
    val title: String,
    val ISBN: String?,
    val description: String?,
    val condition: Condition,
    val seller: Seller
) {

    data class Seller(
        val id: String
    ) {

    }
}