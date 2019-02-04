package cloud.chn.controller

import cloud.chn.service.RequestService
import cloud.chn.service.UrlService
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class UrlController(private val urlService: UrlService, requestService: RequestService) : BaseController(requestService) {

    @GetMapping("/{code}")
    fun getFullUrl(@PathVariable code: String): String {
        return urlService.extract(code)
    }

    @GetMapping("/")
    fun getUrlShorten(@RequestParam url: String, request: HttpServletRequest): String {
        val ip = request.remoteAddr
        checkFrequency(ip)
        increaseFrequency(ip)
        return urlService.shorten(url, ip)
    }
}
