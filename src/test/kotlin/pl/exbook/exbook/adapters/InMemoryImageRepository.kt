package pl.exbook.exbook.adapters

import pl.exbook.exbook.image.domain.Image
import pl.exbook.exbook.image.domain.ImageRepository
import pl.exbook.exbook.shared.ImageId

class InMemoryImageRepository : ImageRepository {
    private val memory = mutableMapOf<ImageId, Image>()

    override fun save(image: Image): Image {
        memory[image.id] = image
        return memory[image.id]!!
    }

    override fun getById(imageId: ImageId): Image? = memory[imageId]

    override fun removeById(imageId: ImageId) {
        memory.remove(imageId)
    }
}
