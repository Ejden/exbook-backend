package pl.exbook.exbook.book

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/books")
@PreAuthorize("isAuthenticated()")
class BookController (private val bookService: BookService) {

    @GetMapping
    @PreAuthorize("hasAuthority('SEARCH_BOOKS')")
    fun getAllBooks() : MutableCollection<Book> {
        return bookService.getAllBooks()
    }

    @PostMapping
    fun addBook(@RequestBody book : Book) : Book {
        return bookService.addBook(book);
    }
}