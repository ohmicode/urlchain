package one.pruned.test.service

import one.pruned.entity.LinkEntity
import one.pruned.exception.ApplicationException
import one.pruned.repository.UrlRepository
import one.pruned.service.UrlService
import one.pruned.test.BaseTest
import org.hamcrest.core.SubstringMatcher
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@Import(UrlService::class)
@RunWith(SpringRunner::class)
class UrlServiceTest : BaseTest() {

    private val DEFAULT_LETTERS = "aA23456789bcdefhkmnprstuvwxyzBCDEFGHKMNPRSTUVWXYZ"

    @Autowired
    private lateinit var urlService: UrlService
    @Autowired
    private lateinit var urlRepository: UrlRepository

    @Test
    fun testAddUrl() {
        val url1 = "http://google.com"
        val result1 = urlService.shorten(url1, "127.0.0.1")
        Assert.assertThat(result1, StringConsistsOf(DEFAULT_LETTERS))
        val stored1 = urlService.extract(result1)
        Assert.assertEquals(url1, stored1)

        val url2 = "http://facebook.com"
        val result2 = urlService.shorten(url2, "127.0.0.1")
        Assert.assertThat(result2, StringConsistsOf(DEFAULT_LETTERS))
        val stored2 = urlService.extract(result2)
        Assert.assertEquals(url2, stored2)
    }

    @Test(expected = ApplicationException::class)
    fun testCodeNotInDb() {
        val stored = urlService.extract("Z")
    }

    @Test(expected = ApplicationException::class)
    fun testWrongCode() {
        val stored = urlService.extract("admin.php")
    }

    @Test(expected = ApplicationException::class)
    fun testCodeWithWrongCharacters() {
        val entityZ = LinkEntity()
        entityZ.id = 48
        entityZ.url = "http://google.com"
        entityZ.source = "127.0.0.1"
        urlRepository.save(entityZ)
        val entityAa = LinkEntity()
        entityAa.id = 49
        entityAa.url = "http://facebook.com"
        entityAa.source = "127.0.0.1"
        urlRepository.save(entityAa)
        val stored = urlService.extract("A0")
    }
}

class StringConsistsOf(substring: String) : SubstringMatcher(substring) {
    override fun relationship(): String {
        return "contains characters from "
    }

    override fun evalSubstringOf(string: String?): Boolean {
        if (string == null) return false
        for (i in 0 until string.length) {
            if (!substring.contains(string[i])) return false
        }
        return true
    }
}
