package pl.exbook.exbook.image.adapter.mongodb

import org.springframework.stereotype.Component
import pl.exbook.exbook.image.domain.ContentType
import pl.exbook.exbook.image.domain.Image
import pl.exbook.exbook.shared.ImageId
import pl.exbook.exbook.image.domain.ImageRepository

@Component
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
    id = this.id.raw,
    file = this.file,
    contentType = this.contentType.toDocument(),
)

private fun ImageDocument.toDomain() = Image(
    id = ImageId(this.id),
    file = this.file,
    contentType = this.contentType.toDomain()
)

private fun ContentType.toDocument() = ContentTypeDocument(
    type = this.type,
    subtype = this.subtype
)

private fun ContentTypeDocument.toDomain() = ContentType(
    type = this.type,
    subtype = this.subtype
)
