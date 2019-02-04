package cloud.chn.controller

import cloud.chn.exception.ApplicationException
import cloud.chn.service.RequestService
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.logging.Logger

open class BaseController(private val requestService: RequestService) {

    val log = Logger.getLogger("Web interface")
    private final val ALLOWED_FREQUENCY = 10

    fun checkFrequency(ip: String) {
        val count = requestService.getLastSecondsCount(ip)
        if (count >= ALLOWED_FREQUENCY) {
            requestService.blockSource(ip)
            throw ApplicationException("too much (more than $count) requests from $ip")
        }
    }

    fun increaseFrequency(ip: String) {
        requestService.increaseFrequency(ip)
    }

    @ExceptionHandler(ApplicationException::class)
    fun logApplicationException(exception: ApplicationException): String {
        log.warning(exception.message)
        return "redirect:error"
    }
}
