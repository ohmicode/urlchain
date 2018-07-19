package cloud.chn.service

import cloud.chn.entity.LinkEntity
import cloud.chn.exception.ApplicationException
import cloud.chn.repository.UrlRepository
import org.springframework.stereotype.Service

@Service
class UrlService(private val urlRepository: UrlRepository) {

    private final val SYMBOLS = "aA23456789bcdefhkmnprstuvwxyzBCDEFGHKMNPRSTUVWXYZ"
    private final val SIZE = SYMBOLS.length

    fun extract(code: String): String {
        val id = decodeId(code)
        val linkOption = urlRepository.findById(id)
        if (linkOption.isPresent) {
            return linkOption.get().url!!
        } else {
            throw ApplicationException("code $code (id=$id) is not found")
        }
    }

    fun shorten(url: String, source: String): String {
        val id = urlRepository.nextKey()

        val linkEntity = LinkEntity()
        linkEntity.id = id
        linkEntity.url = url
        linkEntity.source = source
        urlRepository.save(linkEntity)

        return encodeId(id)
    }

    private fun encodeId(id: Long): String {
        var number = id
        var code = ""
        while (number > 0) {
            val idx = number % SIZE
            code = SYMBOLS[idx.toInt()] + code
            number /= SIZE
        }
        return code
    }

    private fun decodeId(code: String): Long {
        var id = 0L
        for (i in 0 until code.length) {
            val char = code[i]
            val charIndex = SYMBOLS.indexOf(char)
            if (charIndex >= 0)
                id = id * SIZE + charIndex
            else
                throw ApplicationException("illegal character $char found in code $code")
        }
        return id
    }
}
