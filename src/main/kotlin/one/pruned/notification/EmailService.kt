package one.pruned.notification

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Logger
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Service
class EmailService {
    private final val log = Logger.getLogger(EmailService::class.qualifiedName)

    @Value("\${email.smtp.user}")
    private val emailUser: String = "undefined"
    @Value("\${email.smtp.from}")
    private val emailFrom: String = "undefined"
    @Value("\${email.smtp.password}")
    private val emailPassword: String = "undefined"
    @Value("\${email.smtp.host}")
    private val emailHost: String = "undefined"
    @Value("\${email.smtp.port}")
    private val emailPort: String = "undefined"
    @Value("\${email.smtp.auth}")
    private val emailAuth: String = "undefined"
    @Value("\${email.smtp.tls.enable}")
    private val emailTlsEnable: String = "undefined"

    @Value("\${notification.mail.admin}")
    private val adminEmail: String = "undefined"
    @Value("\${notification.mail.subject}")
    private val adminSubject: String = "undefined"

    fun sendAdminMessage(message: String) {
        sendMessage(adminEmail, adminSubject, message)
    }

    fun sendMessage(receiver: String, subject: String, message: String) {
        try {
            val success = pushToEmailServer(receiver, subject, message)
            if (!success) {
                log.warning("Something went wrong sending email to $receiver")
            }
        } catch (unexpected: Exception) {
            log.warning("Can't send email to $receiver, reason: ${unexpected.message}")
        }
    }

    private fun pushToEmailServer(email: String, subject: String, message: String): Boolean {
        val props = buildEmailProperties()
        val session = Session.getInstance(props, null)

        try {
            val username = props["mail.smtp.user"] as String
            val password = props["mail.smtp.password"] as String
            val from = props["mail.smtp.from"] as String

            val m = MimeMessage(session)
            m.setFrom(InternetAddress(from))
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false))
            m.setSentDate(Date())
            m.setSubject(subject)
            m.setContent(message, "text/html; charset=utf-8")

            val transport = session.getTransport("smtp")
            try {
                transport.connect(username, password)
                transport.sendMessage(m, m.getAllRecipients())
                log.info("Email '$subject' sent to $email")
            } finally {
                transport.close()
            }

        } catch (e: MessagingException) {
            log.warning("Can't send email to $email, exception ${e.javaClass.canonicalName}, message: ${e.message}")
            //e.printStackTrace();
            return false
        }

        return true
    }

    private fun buildEmailProperties(): Properties {
        val props = Properties()
        props["mail.smtp.user"] = emailUser
        props["mail.smtp.from"] = emailFrom
        props["mail.smtp.password"] = emailPassword
        props["mail.smtp.host"] = emailHost
        props["mail.smtp.port"] = emailPort
        props["mail.smtp.auth"] = emailAuth
        props["mail.smtp.starttls.enable"] = emailTlsEnable
        props["mail.smtp.ssl.trust"] = emailHost
        props["mail.smtp.timeout"] = "60000"
        props["mail.smtp.connectiontimeout"] = "60000"
        return props
    }
}
