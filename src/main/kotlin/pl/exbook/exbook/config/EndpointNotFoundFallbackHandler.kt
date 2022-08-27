package pl.exbook.exbook.config

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class WebRedirectController {
    @GetMapping(value = ["/", "/{x:[\\w\\-]+}", "/{x:^(?!api\$).*\$}/**/{y:[\\w\\-]+}"])
    fun getIndex() = "/index.html"
}
