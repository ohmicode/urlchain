package one.pruned.repository

import one.pruned.entity.LinkEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UrlRepository : JpaRepository<LinkEntity, Long> {

    @Query("SELECT nextval('link_seq')", nativeQuery = true)
    fun nextKey(): Long
}
