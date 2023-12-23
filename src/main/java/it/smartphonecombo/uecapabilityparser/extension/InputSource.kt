package it.smartphonecombo.uecapabilityparser.extension

import io.javalin.http.UploadedFile
import it.smartphonecombo.uecapabilityparser.io.ByteArrayInputSource
import it.smartphonecombo.uecapabilityparser.io.FileInputSource
import it.smartphonecombo.uecapabilityparser.io.GzipFileInputSource
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.io.StringInputSource
import it.smartphonecombo.uecapabilityparser.io.UploadedFileInputSource
import java.io.File

fun ByteArray.toInputSource() = ByteArrayInputSource(this)

fun String.toInputSource() = StringInputSource(this)

fun File.toInputSource(gzip: Boolean = false) =
    if (gzip) GzipFileInputSource(this) else FileInputSource(this)

fun UploadedFile.toInputSource() = UploadedFileInputSource(this)

@Suppress("NOTHING_TO_INLINE") inline fun InputSource.isEmpty() = size() == 0L
