package it.smartphonecombo.uecapabilityparser.io

import io.javalin.http.UploadedFile
import it.smartphonecombo.uecapabilityparser.extension.toEnumeration
import java.io.File
import java.io.InputStream
import java.io.SequenceInputStream
import java.util.zip.GZIPInputStream

interface InputSource {
    /** it's your responsibility to close this stream */
    fun inputStream(): InputStream

    fun readText(): String

    fun readLines(): List<String>

    fun <T> useLines(block: (Sequence<String>) -> T): T

    fun readBytes(): ByteArray

    /** It's meaning varies according to the implementation * */
    fun size(): Long
}

/**
 * Implements [InputSource] methods using [InputSource.inputStream], so you only need to implement
 * inputStream()
 */
abstract class BasicInputSource : InputSource {
    override fun readText() = inputStream().use { it.reader().readText() }

    override fun readLines() = inputStream().use { it.reader().readLines() }

    override fun <T> useLines(block: (Sequence<String>) -> T): T =
        inputStream().bufferedReader().useLines(block)

    override fun readBytes() = inputStream().use { it.readBytes() }

    override fun size() = inputStream().available().toLong()
}

object NullInputSource : BasicInputSource() {
    override fun inputStream(): InputStream = InputStream.nullInputStream()
}

class SequenceInputSource(private val inputs: List<InputSource>) : BasicInputSource() {
    private val enumeration
        get() = inputs.map(InputSource::inputStream).toEnumeration()

    override fun inputStream() = SequenceInputStream(enumeration)

    override fun size() = inputs.sumOf { it.size() }
}

class UploadedFileInputSource(private val file: UploadedFile) : BasicInputSource() {
    override fun inputStream() = file.content()

    override fun size() = file.size()
}

class GzipFileInputSource(val file: File) : BasicInputSource() {
    override fun inputStream() = GZIPInputStream(file.inputStream(), 4096)

    override fun size() = file.length()
}

class FileInputSource(val file: File) : InputSource {
    override fun inputStream() = file.inputStream()

    override fun readText() = file.readText()

    override fun readLines() = file.readLines()

    override fun <T> useLines(block: (Sequence<String>) -> T): T = file.useLines(block = block)

    override fun readBytes() = file.readBytes()

    override fun size() = file.length()
}

class StringInputSource(private val string: String) : InputSource {
    override fun inputStream() = string.byteInputStream()

    override fun readText() = string

    override fun readLines() = string.lines()

    override fun <T> useLines(block: (Sequence<String>) -> T): T = block(string.lineSequence())

    override fun readBytes() = string.toByteArray()

    override fun size() = string.length.toLong()
}

class ByteArrayInputSource(private val byteArray: ByteArray) : InputSource {
    override fun inputStream() = byteArray.inputStream()

    override fun readText() = byteArray.decodeToString()

    override fun readLines() = byteArray.decodeToString().lines()

    override fun <T> useLines(block: (Sequence<String>) -> T): T =
        byteArray.inputStream().bufferedReader().useLines(block)

    override fun readBytes() = byteArray.copyOf()

    override fun size() = byteArray.size.toLong()
}

fun ByteArray.toInputSource() = ByteArrayInputSource(this)

fun String.toInputSource() = StringInputSource(this)

fun File.toInputSource(gzip: Boolean = false) =
    if (gzip) GzipFileInputSource(this) else FileInputSource(this)

fun UploadedFile.toInputSource() = UploadedFileInputSource(this)

@Suppress("NOTHING_TO_INLINE") inline fun InputSource.isEmpty() = size() == 0L

@Suppress("NOTHING_TO_INLINE") inline fun InputSource.isNotEmpty() = !isEmpty()

@Suppress("NOTHING_TO_INLINE") inline fun InputSource?.isNullOrEmpty() = this == null || isEmpty()
