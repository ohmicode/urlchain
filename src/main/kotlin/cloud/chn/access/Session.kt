package cloud.chn.access

import java.time.LocalDateTime

data class Session (
    var uid: String = "",
    var ip: String = "",
    var expired: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Session) return false
        return uid.equals(other.uid)
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}
