package pl.exbook.exbook.book

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.user.User

interface BookRepository : MongoRepository<BookDatabaseModel, String> {

}

@Document(collection = "book")
data class BookDatabaseModel(
    @Id
    var id: String?,
    var author: String,
    var title: String,
    var ISBN: String?,
    var description: String?,
    var condition: Condition,
    var sellerId: String
) {

    fun toBook() : Book {
        return Book(id, author, title, ISBN, description, condition, User(sellerId))
    }

}
