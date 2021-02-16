package pl.exbook.exbook.controllers

import org.springframework.web.bind.annotation.*
import pl.exbook.exbook.datamodel.Book
import pl.exbook.exbook.services.BookService

@RestController
@RequestMapping("api/v1/books")
class BookController (private val bookService: BookService) {

    @GetMapping
    fun getAllBooks() : MutableCollection<Book> {
        return bookService.getAllBooks()
    }

    @PostMapping
    fun addBook(@RequestBody book : Book) : Book {
        return bookService.addBook(book);
    }
}