package pl.exbook.exbook.image

import org.bson.types.Binary
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Controller
@RequestMapping("api/v1/images")
class ImageController(
    val imageService: ImageService
) {

    @PostMapping
    fun uploadImage(@RequestBody file: MultipartFile): ResponseEntity<Binary> {
        val uploadedImage = imageService.addImage(file)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(uploadedImage.id)
            .toUri()

        return ResponseEntity.created(location).body(uploadedImage.file)
    }

    @DeleteMapping
    fun deleteImage(id: String) {
        imageService.deleteImage(id)
    }

    @GetMapping("{imageId}", produces = ["image/jpeg"])
    fun getImage(@PathVariable imageId: String): ResponseEntity<ByteArray> {
        val image = imageService.getImage(imageId)
        if (image.isPresent) {
            return ResponseEntity.ok(image.get().file.data)
        }

        return ResponseEntity.notFound().build()
    }
}

data class NewImageRequest(
    val image: String
)

class ImageNotFoundException: RuntimeException()
