package cloud.chn.controller

import cloud.chn.service.UrlService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@Profile("local")
@RequestMapping("/admin")
class AdminController(private val urlService: UrlService) {

    @GetMapping("/")
    fun getAdminPage(model: Model): ModelAndView {
        val modelAndView = ModelAndView("admin")
        val links = urlService.getLinks()

        modelAndView.addObject("links", links)
        return modelAndView
    }
}
