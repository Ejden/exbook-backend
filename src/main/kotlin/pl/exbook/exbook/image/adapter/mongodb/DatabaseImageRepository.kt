package pl.exbook.exbook.image.adapter.mongodb

import pl.exbook.exbook.image.domain.Image
import pl.exbook.exbook.shared.ImageId
import pl.exbook.exbook.image.domain.ImageRepository

class DatabaseImageRepository(private val mongoImageRepository: MongoImageRepository) : ImageRepository {

    override fun save(image: Image): Image {
        return mongoImageRepository.save(image.toDocument()).toDomain()
    }

    override fun getById(imageId: ImageId): Image? {
        return mongoImageRepository.findById(imageId.raw)?.toDomain()
    }

    override fun removeById(imageId: ImageId) {
        mongoImageRepository.removeById(imageId.raw)
    }
}

private fun Image.toDocument() = ImageDocument(
    id = this.id?.raw,
    file = this.file
)

private fun ImageDocument.toDomain() = Image(
    id = ImageId(this.id!!),
    file = this.file
)
