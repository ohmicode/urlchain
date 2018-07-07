package cloud.chn.test.service

import cloud.chn.service.UrlService
import cloud.chn.test.BaseTest
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

    @Test
    fun testAddUrl() {
        val result1 = urlService.shorten("http://google.com", "127.0.0.1")
        Assert.assertThat(result1, StringConsistsOf(DEFAULT_LETTERS))

        val result2 = urlService.shorten("http://facebook.com", "127.0.0.1")
        Assert.assertThat(result2, StringConsistsOf(DEFAULT_LETTERS))
    }

    //TODO: more tests
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
