package one.pruned.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler

@Configuration
class RootResourceConfig(private val resourceHttpRequestHandler: ResourceHttpRequestHandler) {

    @Bean
    fun rootResourcesHandlerMapping(): SimpleUrlHandlerMapping {
        val mapping = SimpleUrlHandlerMapping()
        mapping.order = Integer.MIN_VALUE
        mapping.urlMap = hashMapOf("/**/favicon.ico" to resourceHttpRequestHandler,
            "/**/robots.txt" to resourceHttpRequestHandler)
        return mapping
    }
}
