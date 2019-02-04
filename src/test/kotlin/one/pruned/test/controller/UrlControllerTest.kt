package one.pruned.test.controller

import one.pruned.controller.UrlController
import one.pruned.exception.ApplicationException
import one.pruned.test.BaseTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultHandler

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
        val url = "http://facebook.com"
        mockMvc.perform(get("/api/")
            .param("url", url)
            .content(""))
            .andExpect(status().isOk())
            .andDo(assertWasCreated(url))
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
            .andExpect(content().string("redirect:error"))
    }

    @Test
    fun shouldBlockFrequentIp() {
        val ip = "198.162.0.1"
        for (i in 1..10) urlController.increaseFrequency(ip)

        mockMvc.perform(get("/api/")
            .param("url", "http://google.com")
            .with(remoteAddr(ip))
            .content(""))
            .andExpect(content().string("redirect:error"))
    }

    private fun assertWasCreated(url: String): ResultHandler {
        return ResultHandler {
            val code = it.response.contentAsString
            mockMvc.perform(get("/api/" + code)
                .content(""))
                .andExpect(content().string(url))
        }
    }

    // provides fake remote ip
    private fun remoteAddr(remoteAddr: String): RequestPostProcessor {
        return RequestPostProcessor { request ->
            request.remoteAddr = remoteAddr
            request
        }
    }
}
