package cloud.chn.service

import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.logging.Logger

@Service
class PreviewService {
    private final val log = Logger.getLogger(PreviewService::class.qualifiedName)

    private final val META_OG_IMAGE = "<meta property=\"og:image\""
    private final val CONTENT = "content=\""
    private final val QUOTAS = "\""
    private final val PLACEHOLDER_IMG = "/img/placeholder.png"

    fun downloadMetadata(src: String): String {
        val url = unifyUrl(src, "https://")
        val restTemplate = RestTemplate()
        try {
            val response = restTemplate.getForEntity(url, String::class.java)
            if (response.statusCode.is2xxSuccessful && response.hasBody()) {
                val body = response.body!!
                val metadataStart = body.indexOf(META_OG_IMAGE)
                if (metadataStart >= 0) {
                    val urlStart = body.indexOf(CONTENT, metadataStart) + CONTENT.length
                    val urlEnd = body.indexOf(QUOTAS, urlStart)
                    val ogImage = body.substring(urlStart until urlEnd)
                    return unifyUrl(ogImage, url)
                }
            }
        } catch (err: HttpClientErrorException) {
            log.info("unsuccessful restTemplate.getForEntity('$url'), reason: ${err.message}")
        }
        log.info("downloadMetadata fails for url $url")
        return PLACEHOLDER_IMG
    }

    private fun unifyUrl(src: String, prefix: String): String {
        if (src.startsWith("http://") || src.startsWith("https://")) {
            return src
        } else {
            return prefix + src
        }
    }
}
