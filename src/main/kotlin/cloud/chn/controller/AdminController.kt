package cloud.chn.controller

import cloud.chn.service.UrlService
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.sql.Timestamp
import java.text.SimpleDateFormat

@Controller
@Profile("local")
@RequestMapping("/admin")
class AdminController(private val urlService: UrlService) {

    @GetMapping("/")
    fun getAdminPage(pageable: Pageable): ModelAndView {
        val modelAndView = ModelAndView("admin")
        val page = urlService.getLinks(pageable)

        modelAndView.addObject("page", page)
        return modelAndView
    }

    fun getDatesHint(created: Timestamp, updated: Timestamp, expired: Timestamp?): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm")
        val expiredString = if (expired==null) "" else dateFormat.format(expired)
        return "Created: " + dateFormat.format(created) + "\n" +
                "Updated: " + dateFormat.format(created) + "\n" +
                "Expired: " + expiredString
    }
}
