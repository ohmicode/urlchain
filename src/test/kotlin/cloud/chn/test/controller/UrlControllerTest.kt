package cloud.chn.test.controller

import cloud.chn.controller.UrlController
import cloud.chn.exception.ApplicationException
import cloud.chn.test.BaseTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.request.RequestPostProcessor

@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@SpringBootTest
class UrlControllerTest : BaseTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var urlController: UrlController

    @Test
    fun testPutAndGet() {
        mockMvc.perform(get("/api/")
            .param("url", "http://facebook.com")
            .content(""))
            .andExpect(status().isOk())
//            .andDo(assertWasCreated)

        //TODO: assertWasCreated

    }

    @Test(expected = ApplicationException::class)
    fun shouldFailBecauseOfBlockedIp() {
        urlController.checkFrequency("1.1.1.1")
    }

    @Test
    fun shouldRedirectBecauseOfBlockedIp() {
        mockMvc.perform(get("/api/")
            .param("url", "http://google.com")
            .with(remoteAddr("1.1.1.1"))
            .content(""))
            .andExpect(content().string("redirect:error"))  //TODO

    }

    // provides fake remote ip
    private fun remoteAddr(remoteAddr: String): RequestPostProcessor {
        return RequestPostProcessor { request ->
            request.remoteAddr = remoteAddr
            request
        }
    }
}
