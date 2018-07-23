package cloud.chn.controller

import cloud.chn.service.PreviewService
import cloud.chn.service.UrlService
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import java.sql.Timestamp
import java.text.SimpleDateFormat

@Controller
@Profile("local")
@RequestMapping("/admin")
class AdminController(private val urlService: UrlService, private val previewService: PreviewService) {

    @GetMapping("/")
    fun getAdminPage(pageable: Pageable): ModelAndView {
        val modelAndView = ModelAndView("admin")
        val page = urlService.getLinks(pageable)

        modelAndView.addObject("page", page)
        return modelAndView
    }

    @GetMapping("/preview")
    @ResponseBody
    fun getPreviewHtml(@RequestParam id: String): String {
        val entityId = if (id.startsWith("prev")) id.substring(4) else id
        val linkEntity = urlService.findLink(entityId.toLong())
        if (linkEntity.isPresent) {
            val ogImage = previewService.downloadMetadata(linkEntity.get().url!!)
            return "<img class='preview' src='$ogImage'/>"
        } else {
            //TODO: remove sysout
            System.out.println("linkEntity not found for id = " + entityId)
            return "<img class='preview' src='/img/preview.png'/>"
        }
    }

    fun getDatesHint(created: Timestamp, updated: Timestamp, expired: Timestamp?): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm")
        val expiredString = if (expired==null) "" else dateFormat.format(expired)
        return "Created: " + dateFormat.format(created) + "\n" +
                "Updated: " + dateFormat.format(created) + "\n" +
                "Expired: " + expiredString
    }
}
