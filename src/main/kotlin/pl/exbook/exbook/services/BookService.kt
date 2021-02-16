package pl.exbook.exbook.services

import org.springframework.stereotype.Service
import pl.exbook.exbook.datamodel.Book
import pl.exbook.exbook.repositories.BookRepository

@Service
class BookService (private val bookRepository: BookRepository){

    fun getAllBooks() : MutableList<Book> {
        return bookRepository.findAll()
    }

    fun addBook(book: Book) : Book {
        return bookRepository.save(book)
    }
}