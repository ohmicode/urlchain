package one.pruned.service

import org.springframework.stereotype.Service
import java.util.logging.Logger
import org.jsoup.Jsoup
import java.io.IOException

@Service
class PreviewService {
    private final val log = Logger.getLogger(PreviewService::class.qualifiedName)

    private final val META_OG_IMAGE = "meta[property=og:image]"
    private final val CONTENT = "content"
    private final val PLACEHOLDER_IMG = "/img/placeholder.png"

    fun downloadMetadata(src: String): String {
        val url = unifyUrl(src, "https://")
        try {
            val doc = Jsoup.connect(url).get()
            val metaOgImage = doc.select(META_OG_IMAGE)

            if (metaOgImage != null && metaOgImage.hasAttr(CONTENT)) {
                val ogImage = metaOgImage.attr(CONTENT)
                return unifyUrl(ogImage, url)
            }
        } catch (err: IOException) {
            log.info("unsuccessful Jsoup.connect('$url').get(), reason: ${err.message}")
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
