package pl.exbook.exbook.image.domain

import pl.exbook.exbook.shared.ImageId

interface ImageRepository {

    fun save(image: Image): Image

    fun getById(imageId: ImageId): Image?

    fun removeById(imageId: ImageId)
}
