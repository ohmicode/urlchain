package one.pruned.controller

import one.pruned.access.SessionService
import one.pruned.exception.ApplicationException
import one.pruned.service.PreviewService
import one.pruned.service.UrlService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.fasterxml.jackson.databind.ObjectMapper
import one.pruned.notification.NotificationService
import java.security.MessageDigest

@Controller
@RequestMapping("/office")
class AdminController(private val urlService: UrlService,
                      private val previewService: PreviewService,
                      private val sessionService: SessionService,
                      private val notificationService: NotificationService) {

    private final val log = Logger.getLogger("Office interface")

    private final val SID = "sid"
    private final val SHA_TYPE = "SHA-256"

    @Value("\${users.friend}")
    internal val goodFriend = UUID.randomUUID().toString()
    @Value("\${users.word}")
    internal val goodWord = UUID.randomUUID().toString()

    @GetMapping("/welcome")
    fun getWelcomePage(request: HttpServletRequest): String {
        return "welcome"
    }

    @PostMapping("/welcome")
    fun postWelcomePage(@RequestParam friend: String, @RequestParam word: String, request: HttpServletRequest): String {
        throw ApplicationException("Suspicious activity from IP ${request.remoteAddr}")
    }

    @PostMapping("/check")
    @ResponseBody
    fun checkGuest(@RequestBody body: String, request: HttpServletRequest, response: HttpServletResponse): String {
        val objectMapper = ObjectMapper()
        val guest = objectMapper.readValue(body, Guest::class.java)

        if (guest.friend == goodFriend && hash(guest.word) == goodWord) {
            val sid = sessionService.createSession(request.remoteAddr)
            response.addCookie(Cookie(SID, sid))
            notificationService.push("Master has entered the Office from ${request.remoteAddr}")
            return "<h3>welcome back, Master</h3>"
        } else {
            throw ApplicationException("Wrong try for office/welcome: $body from IP ${request.remoteAddr}")
        }
    }

    @GetMapping("/")
    fun getAdminPage(pageable: Pageable, request: HttpServletRequest): ModelAndView {
        validateSession(request)

        val modelAndView = ModelAndView("office")
        val page = urlService.getLinks(pageable)

        modelAndView.addObject("page", page)
        return modelAndView
    }

    @GetMapping("/preview")
    @ResponseBody
    fun getPreviewHtml(@RequestParam id: String, request: HttpServletRequest): String {
        validateSessionFast(request)

        val entityId = if (id.startsWith("prev")) id.substring(4) else id
        val linkEntity = urlService.findLink(entityId.toLong())
        if (linkEntity.isPresent) {
            val ogImage = previewService.downloadMetadata(linkEntity.get().url!!)
            return "<img class='preview' src='$ogImage'/>"
        } else {
            return "<img class='preview' src='/img/bug.png'/>"
        }
    }

    fun getDatesHint(created: Timestamp, updated: Timestamp, expired: Timestamp?): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm")
        val expiredString = if (expired==null) "" else dateFormat.format(expired)
        return "Created: " + dateFormat.format(created) + "\n" +
                "Updated: " + dateFormat.format(created) + "\n" +
                "Expired: " + expiredString
    }

    @ExceptionHandler(ApplicationException::class)
    fun logApplicationException(exception: ApplicationException): String {
        log.warning(exception.message)
        return "redirect:error"
    }

    private fun validateSession(request: HttpServletRequest) {
        val sessionUid = extractSessionUid(request.cookies)
        val remoteIp = request.remoteAddr
        if (!sessionService.validateAndPrune(sessionUid, remoteIp)) {
            throw ApplicationException("wrong session $sessionUid from ip $remoteIp")
        }
    }

    private fun validateSessionFast(request: HttpServletRequest) {
        val sessionUid = extractSessionUid(request.cookies)
        val remoteIp = request.remoteAddr
        if (!sessionService.validate(sessionUid, remoteIp)) {
            throw ApplicationException("wrong session $sessionUid from ip $remoteIp")
        }
    }

    private fun extractSessionUid(cookies: Array<Cookie>): String {
        for (cookie in cookies) {
            if (SID == cookie.name) return cookie.value
        }
        return "unknown"
    }

    private fun hash(input: String) =
        MessageDigest
            .getInstance(SHA_TYPE)
            .digest(input.toByteArray())
            .map { String.format("%02X", it) }
            .joinToString("")
}

class Guest {
    val friend: String = ""
    val word: String = ""
}
