package pl.exbook.exbook.ability

import org.bson.types.Binary
import org.springframework.mock.web.MockMultipartFile
import pl.exbook.exbook.adapters.InMemoryImageRepository
import pl.exbook.exbook.image.ImageFacade
import pl.exbook.exbook.image.domain.Image
import pl.exbook.exbook.shared.TestData.sampleImageBinary

class ImageDomainAbility {
    private val imageRepository: InMemoryImageRepository = InMemoryImageRepository()
    val facade: ImageFacade = ImageFacade(imageRepository)

    fun thereIsImage(
        name: String = "filename",
        contentType: String = "image/png",
        binary: Binary = sampleImageBinary
    ): Image {
        return facade.addImage(MockMultipartFile(name, name, contentType, binary.data))
    }
}
