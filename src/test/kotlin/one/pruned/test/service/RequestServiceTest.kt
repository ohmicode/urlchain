package one.pruned.test.service

import one.pruned.service.RequestService
import one.pruned.test.BaseTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@Import(RequestService::class)
@RunWith(SpringRunner::class)
class RequestServiceTest : BaseTest() {

    @Autowired
    private lateinit var requestService: RequestService

    @Test
    fun testIncreaseAndCount() {
        val ip = "7.7.7.7"
        requestService.increaseFrequency(ip)
        var count = requestService.getLastSecondsCount(ip)
        Assert.assertEquals(1, count)

        requestService.increaseFrequency(ip)
        count = requestService.getLastSecondsCount(ip)
        Assert.assertEquals(2, count)

        requestService.increaseFrequency(ip)
        requestService.increaseFrequency(ip)
        requestService.increaseFrequency(ip)
        count = requestService.getLastSecondsCount(ip)
        Assert.assertEquals(5, count)
    }

    @Test
    fun testCountOfNewAddress() {
        val ip = "9.9.9.9"
        val count = requestService.getLastSecondsCount(ip)
        Assert.assertEquals(1, count)
    }

    @Test
    fun testBlockingAddresses() {
        var count = requestService.getLastSecondsCount("1.1.1.1")
        Assert.assertEquals(Int.MAX_VALUE, count)

        val ip = "10.0.0.1"
        requestService.increaseFrequency(ip)
        count = requestService.getLastSecondsCount(ip)
        Assert.assertEquals(1, count)
        requestService.blockSource(ip)
        count = requestService.getLastSecondsCount(ip)
        Assert.assertEquals(Int.MAX_VALUE, count)
    }
}
