package pl.exbook.exbook.image.adapter.rest

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pl.exbook.exbook.image.ImageFacade
import pl.exbook.exbook.shared.ImageId
import pl.exbook.exbook.shared.ContentType

@Controller
@RequestMapping("api/images")
class ImageEndpoint(val imageFacade: ImageFacade) {
    @PostMapping(produces = [ContentType.V1])
    fun uploadImage(@RequestBody file: MultipartFile?): ResponseEntity<Any> {
        val uploadedImage = imageFacade.addImage(file!!)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(uploadedImage.id.raw)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @DeleteMapping(produces = [ContentType.V1])
    fun deleteImage(imageId: ImageId) {
        imageFacade.deleteImage(imageId)
    }

    @GetMapping("{imageId}")
    fun getImage(@PathVariable imageId: ImageId): ResponseEntity<ByteArray> {
        val image = imageFacade.getImage(imageId)
        return ResponseEntity
            .ok()
            .contentType(MediaType("image", image.contentType.subtype))
            .body(image.file.data)
    }
}
