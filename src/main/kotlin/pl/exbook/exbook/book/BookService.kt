package pl.exbook.exbook.book

import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import pl.exbook.exbook.user.User
import pl.exbook.exbook.user.UserService
import java.util.stream.Collectors

private val logger = KotlinLogging.logger {}

@Service
class BookService (
    private val bookRepository: BookRepository,
    private val userService: UserService
){

    fun getAllBooks() : MutableList<Book> {
        return bookRepository.findAll()
            .stream()
            .map(BookDatabaseModel::toBook)
            .collect(Collectors.toList())
    }

    fun addBook(request: NewBookRequest, token: UsernamePasswordAuthenticationToken) : Book {
        // Getting user from database that sent
        val user : User? = userService.findUserByUsername(token.name)

        val book =  bookRepository.save(
            BookDatabaseModel(
                id = null,
                author = request.author,
                title = request.title,
                ISBN = request.ISBN,
                description = request.description,
                condition = request.condition,
                sellerId = user?.id!!
            )
        ).toBook()

        logger.debug("User with id = ${user.id} added new book with id = ${book.id}")

        return book
    }
}


