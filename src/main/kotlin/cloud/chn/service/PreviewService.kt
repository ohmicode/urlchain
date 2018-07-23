package cloud.chn.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class PreviewService {
    private final val META_OG_IMAGE = "<meta property=\"og:image\""
    private final val CONTENT = "content=\""
    private final val QUOTAS = "\""
    private final val PLACEHOLDER_SRC = "/img/placeholder.png"

    fun downloadMetadata(src: String): String {
        val url = if (src.startsWith("http://")) src else "http://$src"
        val restTemplate = RestTemplate()
        val response = restTemplate.getForEntity(url, String::class.java)
        if (response.statusCode.is2xxSuccessful && response.hasBody()) {
            val body = response.body!!
            val metadataStart = body.indexOf(META_OG_IMAGE)
            if (metadataStart >= 0) {
                val urlStart = body.indexOf(CONTENT, metadataStart) + CONTENT.length
                val urlEnd = body.indexOf(QUOTAS, urlStart)
                return body.substring(urlStart until urlEnd)
            }
        }
        //TODO: remove sysout
        System.out.println("downloadMetadata fails for url = " + url)
        return PLACEHOLDER_SRC
    }
}
