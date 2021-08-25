package pl.exbook.exbook.image

import org.bson.BsonBinarySubType
import org.bson.types.Binary
import org.springframework.web.multipart.MultipartFile
import pl.exbook.exbook.image.domain.Image
import pl.exbook.exbook.shared.ImageId
import pl.exbook.exbook.image.domain.ImageRepository

class ImageFacade(
    private val imageRepository: ImageRepository
) {
    fun addImage(file: MultipartFile): Image {
        val image = Image(null, Binary(BsonBinarySubType.BINARY, file.bytes))
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
