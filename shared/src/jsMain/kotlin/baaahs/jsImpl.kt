package baaahs

import baaahs.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.w3c.dom.get
import web.encoding.atob
import kotlin.math.min

actual fun <T> doRunBlocking(block: suspend () -> T): T {
    var value: T? = null
    GlobalScope.promise {
        value = block()
    }
        .catch { t ->
            Logger("doRunBlocking").error(t) { "Error during doRunBlocking()" }
        }
    return value!!
}

val resourcesBase: String get() = kotlinx.browser.document["resourcesBase"]

actual fun decodeBase64(s: String): ByteArray =
    atob(s).let { binaryStr ->
        ByteArray(binaryStr.length) { i ->
            binaryStr.asDynamic().charCodeAt(i)
        }
    }

// TODO: this is probably busted, fix!
actual fun encodeBase64(b: ByteArray): String {
    val b64 = encode(b)
    return StringBuilder().apply {
        for (element in b64) {
            append(element.toInt().toChar())
        }
    }.toString()
}

val doPadding = true
val isURL = false
val linemax = 0

private val toBase64 = charArrayOf(
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
)

private val toBase64URL = charArrayOf(
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
)

private fun encodeBlock(src: ByteArray, sp: Int, sl: Int, dst: ByteArray, dp: Int, isURL: Boolean) {
    val base64: CharArray = if (isURL) toBase64URL else toBase64
    var sp0 = sp
    var dp0 = dp
    while (sp0 < sl) {
        val bits = src[sp0++].toInt() and 0xff shl 16 or (
                src[sp0++].toInt() and 0xff shl 8) or
                (src[sp0++].toInt() and 0xff)
        dst[dp0++] = base64[bits ushr 18 and 0x3f].code.toByte()
        dst[dp0++] = base64[bits ushr 12 and 0x3f].code.toByte()
        dst[dp0++] = base64[bits ushr 6 and 0x3f].code.toByte()
        dst[dp0++] = base64[bits and 0x3f].code.toByte()
    }
}

val newline = arrayOf('\n')
private fun encode0(src: ByteArray, off: Int, end: Int, dst: ByteArray): Int {
    val base64: CharArray = if (isURL) toBase64URL else toBase64
    var sp = off
    var slen = (end - off) / 3 * 3
    val sl = off + slen
    if (linemax > 0 && slen > linemax / 4 * 3) slen = linemax / 4 * 3
    var dp = 0
    while (sp < sl) {
        val sl0: Int = min(sp + slen, sl)
        encodeBlock(src, sp, sl0, dst, dp, isURL)
        val dlen = (sl0 - sp) / 3 * 4
        dp += dlen
        sp = sl0
        if (dlen == linemax && sp < end) {
            for (b in newline) {
                dst[dp++] = b.code.toByte()
            }
        }
    }
    if (sp < end) {               // 1 or 2 leftover bytes
        val b0 = src[sp++].toInt() and 0xff
        dst[dp++] = base64[b0 shr 2].code.toByte()
        if (sp == end) {
            dst[dp++] = base64[b0 shl 4 and 0x3f].code.toByte()
            if (doPadding) {
                dst[dp++] = '='.code.toByte()
                dst[dp++] = '='.code.toByte()
            }
        } else {
            val b1 = src[sp++].toInt() and 0xff
            dst[dp++] = base64[b0 shl 4 and 0x3f or (b1 shr 4)].code.toByte()
            dst[dp++] = base64[b1 shl 2 and 0x3f].code.toByte()
            if (doPadding) {
                dst[dp++] = '='.code.toByte()
            }
        }
    }
    return dp
}

private fun encodedOutLength(srclen: Int): Int = 4 * (srclen + 2) / 3

fun encode(src: ByteArray): ByteArray {
    val len: Int = encodedOutLength(src.size) // dst array size
    val dst = ByteArray(len)
    val ret: Int = encode0(src, 0, src.size, dst)
    return if (ret != dst.size) dst.copyOf(ret) else dst
}
