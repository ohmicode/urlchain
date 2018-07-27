package cloud.chn.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.WebApplicationInitializer
import java.util.*
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.SessionTrackingMode

@Configuration
class WebConfig : WebApplicationInitializer {
    @Throws(ServletException::class)
    override fun onStartup(servletContext: ServletContext) {
        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE))
    }
}
