package pl.exbook.exbook.image.domain

import pl.exbook.exbook.shared.ImageId
import pl.exbook.exbook.shared.NotFoundException

data class ImageNotFoundException(val imageId: ImageId) : NotFoundException("Image with id ${imageId.raw} not found")
