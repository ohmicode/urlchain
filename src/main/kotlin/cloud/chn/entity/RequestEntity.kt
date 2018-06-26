package cloud.chn.entity

import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "request")
class RequestEntity(forSource: String) {

    constructor(): this("unknown")

    @Id
    var source: String = forSource

    var lastTime: Timestamp = Timestamp.from(Instant.now())

    var blockedUntil: Timestamp? = null

    var count: Int = 1
}
