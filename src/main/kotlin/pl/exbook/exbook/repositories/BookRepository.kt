package pl.exbook.exbook.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.datamodel.Book

interface BookRepository : MongoRepository<Book, String> {

}