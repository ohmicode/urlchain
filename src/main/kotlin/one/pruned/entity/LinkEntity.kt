package cloud.chn.entity

import java.sql.Timestamp
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "link")
class LinkEntity {

    @Id
    var id: Long? = null

    var created: Timestamp = Timestamp.from(Instant.now())

    var updated: Timestamp =  Timestamp.from(Instant.now())

    var expired: Timestamp? = null

    var url: String? = null

    var source: String? = null

    var permanent: Boolean = true

    var flagged: Boolean = false

    var test: Boolean = false
}
