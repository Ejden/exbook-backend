package pl.exbook.exbook.features.image

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.springframework.mock.web.MockMultipartFile
import pl.exbook.exbook.ability.ImageDomainAbility
import pl.exbook.exbook.image.domain.ContentTypeNotSupportedException
import pl.exbook.exbook.image.domain.ImageNotFoundException
import pl.exbook.exbook.shared.TestData.sampleImageBinary
import pl.exbook.exbook.shared.TestData.sampleImageId

class ImageFacadeSpec : ShouldSpec({
    val domain = ImageDomainAbility()

    should("add image") {
        // given
        val file = MockMultipartFile("image", "image", "image/png", sampleImageBinary.data)

        // when
        val result = domain.facade.addImage(file)
        val image = domain.facade.getImage(result.id)

        // then
        image.file shouldBe sampleImageBinary
        image.contentType.type shouldBe "image"
        image.contentType.subtype shouldBe "png"
    }

    should("throw an error when trying to add image without correct content type") {
        // given
        val file = MockMultipartFile("image", "image", "file/png", sampleImageBinary.data)

        // expect
        shouldThrowExactly<ContentTypeNotSupportedException> {
            domain.facade.addImage(file)
        }
    }

    should("get image") {
        // given
        val id = domain.thereIsImage(binary = sampleImageBinary, contentType = "image/jpeg").id

        // when
        val image = domain.facade.getImage(id)

        // then
        image.file shouldBe sampleImageBinary
        image.contentType.type shouldBe "image"
        image.contentType.subtype shouldBe "jpeg"
    }

    should("throw an error when trying to get non existing image") {
        // expect
        shouldThrowExactly<ImageNotFoundException> {
            domain.facade.getImage(sampleImageId)
        }
    }

    should("delete image") {
        // given
        val id = domain.thereIsImage(binary = sampleImageBinary, contentType = "image/jpeg").id

        // when
        domain.facade.deleteImage(id)

        // then
        shouldThrowExactly<ImageNotFoundException> {
            domain.facade.getImage(id)
        }
    }
})
