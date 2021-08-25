package pl.exbook.exbook.image.adapter.rest

import org.bson.types.Binary
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pl.exbook.exbook.image.ImageFacade
import pl.exbook.exbook.shared.ImageId
import pl.exbook.exbook.shared.MediaType

@Controller
@RequestMapping("api/v1/images")
class ImageEndpoint(
    val imageFacade: ImageFacade
) {

    @PostMapping(produces = [MediaType.V1])
    fun uploadImage(@RequestBody file: MultipartFile): ResponseEntity<Binary> {
        val uploadedImage = imageFacade.addImage(file)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(uploadedImage.id)
            .toUri()

        return ResponseEntity.created(location).body(uploadedImage.file)
    }

    @DeleteMapping(produces = [MediaType.V1])
    fun deleteImage(imageId: ImageId) {
        imageFacade.deleteImage(imageId)
    }

    @GetMapping("{imageId}", produces = ["image/jpeg"])
    fun getImage(@PathVariable imageId: ImageId): ResponseEntity<ByteArray> {
        return ResponseEntity.ok(imageFacade.getImage(imageId).file.data)
    }
}
