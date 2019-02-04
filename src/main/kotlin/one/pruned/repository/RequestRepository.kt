package cloud.chn.repository

import cloud.chn.entity.RequestEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RequestRepository : JpaRepository<RequestEntity, String> {
}
