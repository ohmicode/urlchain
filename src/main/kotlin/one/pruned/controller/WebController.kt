package one.pruned.controller

import one.pruned.exception.ApplicationException
import one.pruned.service.RequestService
import one.pruned.service.UrlService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/")
class WebController(private val urlService: UrlService, requestService: RequestService) : BaseController(requestService) {

    @Value("\${site.url}")
    internal val SITE_URL = ""


    @GetMapping("/{code}")
    fun getFullUrl(@PathVariable code: String): ModelAndView {
        val url = normalize(urlService.extract(code))
        return ModelAndView("redirect:$url")
    }

    @GetMapping("/")
    fun getHomePage(model: Model): String {
        return "mainpage"
    }

    @PostMapping("/")
    fun getUrlShorten(@RequestParam url: String, attributes: RedirectAttributes, request: HttpServletRequest): String {
        if (url.trim().isEmpty()) throw ApplicationException("url parameter is not specified")

        val ip = request.remoteAddr
        checkFrequency(ip)
        increaseFrequency(ip)

        val code = urlService.shorten(url.trim(), ip)
        attributes.addFlashAttribute("hasResult", true)
        attributes.addFlashAttribute("code", withSiteName(code))
        attributes.addFlashAttribute("url", normalize(url.trim()))
        return "redirect:/"
    }

    private fun withSiteName(code: String) = SITE_URL + code

    private fun normalize(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://")) url else "http://$url"
    }
}
