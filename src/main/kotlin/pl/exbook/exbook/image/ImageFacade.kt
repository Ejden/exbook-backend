package pl.exbook.exbook.image

import org.bson.BsonBinarySubType
import org.bson.types.Binary
import org.springframework.web.multipart.MultipartFile
import pl.exbook.exbook.image.domain.ContentType
import pl.exbook.exbook.image.domain.Image
import pl.exbook.exbook.shared.ImageId
import pl.exbook.exbook.image.domain.ImageRepository

class ImageFacade(
    private val imageRepository: ImageRepository
) {

    companion object {
        private val contentTypeRegex = Regex("^(image)/(.*)$")
    }

    fun addImage(file: MultipartFile): Image {
        val subtype = file.contentType?.let { contentTypeRegex.matchEntire(it)?.groups }?.get(2)?.value
        val image = Image(
            id = null,
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
}

data class ImageNotFoundException(val imageId: ImageId) : RuntimeException("Image with id ${imageId.raw} not found")
data class ContentTypeNotSupportedException(val msg: String) : RuntimeException(msg)