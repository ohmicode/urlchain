package one.pruned.notification

import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URLEncoder
import java.util.*
import java.util.logging.Logger

// HOWTO get token & chatId
// Inside Telegram go to bot @BotFather
// create a bot with /newbot (get token here)
// start new chat with that bot
// curl 'https://api.telegram.org/bot$token/getUpdates'
// Chat id will be in response JSON
@Service
class TelegramService {
    private final val log = Logger.getLogger(TelegramService::class.qualifiedName)

    @Value("\${notification.telegram.token}")
    internal val token = UUID.randomUUID().toString()
    @Value("\${notification.telegram.chat}")
    internal val chatId = UUID.randomUUID().toString()

    fun sendMessage(message: String) {
        val urlEncodedMessage = URLEncoder.encode(message, "UTF-8")
        val url = "https://api.telegram.org/bot$token/sendMessage?chat_id=$chatId&text=$urlEncodedMessage"
        try {
            val response = Jsoup.connect(url).ignoreContentType(true).get()
            // do we need to check response?
        } catch (err: IOException) {
            log.warning("Cannot notify through Telegram, reason: ${err.message}")
        }
    }
}
