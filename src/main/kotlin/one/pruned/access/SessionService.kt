package cloud.chn.access

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.logging.Logger
import kotlin.streams.toList

@Service
class SessionService {
    private final val log = Logger.getLogger(SessionService::class.qualifiedName)

    private val sessions = HashSet<Session>()
    private val lock = ReentrantReadWriteLock()

    fun validate(sessionUid: String, sessionIp: String): Boolean {
        lock.readLock().lock()
        val session = sessions.stream()
            .filter {it.uid.equals(sessionUid)}
            .findFirst()
        lock.readLock().unlock()
        if (session.isPresent && !session.get().ip.equals(sessionIp)) {
            log.warning("Session $sessionUid was created for IP ${session.get().ip} but used from IP $sessionIp")
        }
        val now = LocalDateTime.now()
        return session.isPresent && session.get().expired.isAfter(now)
    }

    fun validateAndPrune(sessionUid: String, sessionIp: String): Boolean {
        prune()
        return validate(sessionUid, sessionIp)
    }

    fun createSession(ip: String): String {
        prune()
        val uid = generateUid()
        val expired = LocalDateTime.now().plusHours(12)
        val session = Session(uid, ip, expired)
        lock.writeLock().lock()
        sessions.add(session)
        lock.writeLock().unlock()

        log.info("new Session $uid was created for IP $ip")
        return uid
    }

    private fun prune() {
        val now = LocalDateTime.now()
        lock.writeLock().lock()
        val toBeDeleted = sessions.stream()
            .filter { it.expired.isBefore(now) }
            .toList()
        sessions.removeAll(toBeDeleted)
        lock.writeLock().unlock()
    }

    private fun generateUid(): String {
        var uuid = UUID.randomUUID().toString()
        lock.readLock().lock()
        while (sessions.stream().anyMatch { it.uid.equals(uuid) }) {
            uuid = UUID.randomUUID().toString()
        }
        lock.readLock().unlock()
        return uuid
    }
}
