package one.pruned.notification

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

// This service uses coroutines for async calls
@Service
class NotificationService(private val telegramService: TelegramService,
                          private val emailService: EmailService) {

    // immediately inform admin through instant messenger
    fun push(message: String) {
        GlobalScope.launch {
            telegramService.sendMessage(message)
        }
    }

    // (for your information)
    // inform admin through any slow message system
    fun fyi(message: String) {
        GlobalScope.launch {
            emailService.sendAdminMessage(message)
        }
    }
}
