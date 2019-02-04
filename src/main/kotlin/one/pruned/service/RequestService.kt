package cloud.chn.service

import cloud.chn.entity.RequestEntity
import org.springframework.stereotype.Service
import cloud.chn.repository.RequestRepository
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime

@Service
class RequestService(private val requestRepository: RequestRepository) {

    private final val BLOCKING_TIME = 60L  //min

    fun getLastSecondsCount(source: String): Int {
        val requestOptional = requestRepository.findById(source)
        if (requestOptional.isPresent) {
            val requestEntity = requestOptional.get()
            if (isBlocked(requestEntity)) {
                return Int.MAX_VALUE
            } else if (lastSecond(requestEntity)) {
                return requestOptional.get().count
            } else {
                return 1
            }
        } else {
            return 1
        }
    }

    fun increaseFrequency(source: String) {
        val requestEntity = requestRepository.findById(source).orElse(RequestEntity(source))
        if (!isBlocked(requestEntity)) {
            //race condition, but not critical
            requestEntity.blockedUntil = null
            requestEntity.count = if (lastSecond(requestEntity)) requestEntity.count + 1 else 1
            requestEntity.lastTime = Timestamp.valueOf(LocalDateTime.now())
        }
        requestRepository.save(requestEntity)
    }

    fun blockSource(source: String) {
        val requestEntity = requestRepository.findById(source).orElse(RequestEntity(source))
        if (!isBlocked(requestEntity)) {
            requestEntity.blockedUntil = Timestamp.valueOf(LocalDateTime.now().plusMinutes(BLOCKING_TIME))
            requestRepository.save(requestEntity)
        }
    }

    private fun lastSecond(requestEntity: RequestEntity): Boolean {
        return System.currentTimeMillis() - requestEntity.lastTime.time <= 1000
    }

    private fun isBlocked(requestEntity: RequestEntity): Boolean {
        val blockedUntil = requestEntity.blockedUntil
        return blockedUntil != null && blockedUntil.after(Timestamp.from(Instant.now()))
    }
}
