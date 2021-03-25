package pl.exbook.exbook.admin

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
@PreAuthorize("hasRole('ADMIN')")
class AdminController {

}