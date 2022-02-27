package pl.exbook.exbook.image

import java.util.UUID
import org.bson.BsonBinarySubType
import org.bson.types.Binary
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.exbook.exbook.image.domain.ContentType
import pl.exbook.exbook.image.domain.ContentTypeNotSupportedException
import pl.exbook.exbook.image.domain.Image
import pl.exbook.exbook.image.domain.ImageNotFoundException
import pl.exbook.exbook.shared.ImageId
import pl.exbook.exbook.image.domain.ImageRepository

@Service
class ImageFacade(private val imageRepository: ImageRepository) {
    fun addImage(file: MultipartFile): Image {
        val subtype = file.contentType?.let { contentTypeRegex.matchEntire(it)?.groups }?.get(2)?.value

        val image = Image(
            id = ImageId(UUID.randomUUID().toString()),
            file = Binary(BsonBinarySubType.BINARY, file.bytes),
            contentType = ContentType("image", subtype
                ?: throw ContentTypeNotSupportedException("Image type not supported")
            )
        )
        return imageRepository.save(image)
    }

    fun deleteImage(imageId: ImageId) {
        imageRepository.removeById(imageId)
    }

    fun getImage(imageId: ImageId): Image {
        return imageRepository.getById(imageId) ?: throw ImageNotFoundException(imageId)
    }

    companion object {
        private val contentTypeRegex = Regex("^(image)/(.*)$")
    }
}
