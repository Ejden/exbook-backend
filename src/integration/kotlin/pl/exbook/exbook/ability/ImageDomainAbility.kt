package pl.exbook.exbook.ability

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import pl.exbook.exbook.shared.TestData.sampleFile
import pl.exbook.exbook.utils.createHttpEntity

class ImageDomainAbility(private val restTemplate: TestRestTemplate) {
    fun thereIsImage(file: MultipartFile = sampleFile): String {
        return restTemplate.postForEntity(
            "/api/images",
            createHttpEntity(body = file, withAcceptHeader = true),
            Any::class.java
        ).headers["location"].toString()
    }

    fun addImage(file: MultipartFile = sampleFile): ResponseEntity<Any> {
        return restTemplate.postForEntity(
            "/api/images",
            createHttpEntity(body = file, withAcceptHeader = true),
            Any::class.java
        )
    }

    fun getImage(imageId: String): ResponseEntity<String> {
        return restTemplate.getForEntity("/api/images/$imageId", String::class.java)
    }

    fun deleteImage(imageId: String): ResponseEntity<Unit> {
        return restTemplate.exchange("/api/images/$imageId", HttpMethod.DELETE, null, Unit::class.java)
    }
}
