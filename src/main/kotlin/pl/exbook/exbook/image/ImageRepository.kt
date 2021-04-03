package pl.exbook.exbook.image

import org.springframework.data.mongodb.repository.MongoRepository

interface ImageRepository: MongoRepository<Image, String>
