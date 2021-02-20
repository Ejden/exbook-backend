package pl.exbook.exbook.book

import org.springframework.stereotype.Service

@Service
class BookService (private val bookRepository: BookRepository){

    fun getAllBooks() : MutableList<Book> {
        return bookRepository.findAll()
    }

    fun addBook(book: Book) : Book {
        return bookRepository.save(book)
    }
}