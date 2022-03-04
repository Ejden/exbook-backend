package pl.exbook.exbook.features

import org.springframework.boot.test.web.client.TestRestTemplate
import pl.exbook.exbook.BaseIntegrationSpec
import pl.exbook.exbook.ability.ImageDomainAbility

class ImageEndpointSpec(private val rest: TestRestTemplate) : BaseIntegrationSpec({
    val domain = ImageDomainAbility(rest)

    xshould("get image") {

    }

    xshould("return status 404 when image was not found") {

    }

    xshould("return status 422 when trying to add unsupported file") {

    }

    xshould("delete image") {

    }

    xshould("return status 404 when trying to delete non existing image") {

    }
})
