package pl.exbook.exbook.image.adapter.mongodb

import org.springframework.data.mongodb.repository.MongoRepository
import pl.exbook.exbook.image.domain.Image

interface ImageRepository: MongoRepository<Image, String>
