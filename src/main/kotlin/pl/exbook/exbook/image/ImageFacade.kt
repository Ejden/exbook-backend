package pl.exbook.exbook.image

import org.bson.BsonBinarySubType
import org.bson.types.Binary
import org.springframework.web.multipart.MultipartFile
import pl.exbook.exbook.image.adapter.mongodb.ImageRepository
import pl.exbook.exbook.image.domain.Image
import java.util.*

class ImageFacade(
    private val imageRepository: ImageRepository
) {
    fun addImage(file: MultipartFile): Image {
        val image = Image(null, Binary(BsonBinarySubType.BINARY, file.bytes))
        return imageRepository.save(image)
    }

    fun deleteImage(id: String) {
        imageRepository.deleteById(id)
    }

    fun getImage(id: String): Optional<Image> {
        return imageRepository.findById(id)
    }
}