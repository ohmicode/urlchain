package one.pruned.repository

import one.pruned.entity.RequestEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RequestRepository : JpaRepository<RequestEntity, String> {
}
