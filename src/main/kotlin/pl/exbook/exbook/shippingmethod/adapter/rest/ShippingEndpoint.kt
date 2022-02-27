package pl.exbook.exbook.shippingmethod.adapter.rest

import javax.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.exbook.exbook.shared.ContentType
import pl.exbook.exbook.shippingmethod.ShippingMethodFacade
import pl.exbook.exbook.shippingmethod.adapter.rest.dto.NewShippingMethodRequest
import pl.exbook.exbook.shippingmethod.adapter.rest.dto.ShippingMethodDto
import pl.exbook.exbook.shippingmethod.domain.ShippingMethod

@RestController
@RequestMapping("api/shipping")
class ShippingEndpoint(
    private val shippingMethodFacade: ShippingMethodFacade
) {
    @GetMapping(produces = [ContentType.V1])
    fun getShippingMethods(): List<ShippingMethodDto> {
        return shippingMethodFacade.getShippingMethods().map { it.toDto() }
    }

    @PostMapping(produces = [ContentType.V1], consumes = [ContentType.V1])
    fun addShippingMethod(@RequestBody @Valid requestBody: NewShippingMethodRequest): ShippingMethodDto {
        return shippingMethodFacade.addShippingMethod(requestBody.toCommand()).toDto()
    }
}

private fun ShippingMethod.toDto() = ShippingMethodDto.fromDomain(this)
