package it.smartphonecombo.uecapabilityparser.extension

import io.javalin.config.SizeUnit
import io.javalin.http.ContentType
import io.javalin.http.Context
import io.javalin.http.HttpResponseException
import io.javalin.http.HttpStatus
import io.javalin.http.bodyAsClass
import io.javalin.http.bodyStreamAsClass
import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.io.InputSource

internal fun Context.badRequest(message: String = "Bad Request") {
    result(message)
    status(HttpStatus.BAD_REQUEST)
}

internal fun Context.notFound(message: String = "Not Found") {
    result(message)
    status(HttpStatus.NOT_FOUND)
}

internal fun Context.internalError(message: String = "Internal Server Error") {
    result(message)
    status(HttpStatus.INTERNAL_SERVER_ERROR)
}

internal fun Context.attachFile(data: InputSource, filename: String, contentType: ContentType) {
    result(data.inputStream())
        .contentType(contentType)
        .header("Content-Disposition", "attachment; filename=$filename")
        .header("Access-Control-Expose-Headers", "Content-Disposition")
}

internal fun Context.throwContentTooLargeIfContentTooLarge(maxRequestSize: Long) {
    if (req().contentLengthLong > maxRequestSize) {
        echoSafe("Body greater than max size ($maxRequestSize bytes)", err = true)
        throw HttpResponseException(
            HttpStatus.CONTENT_TOO_LARGE,
            HttpStatus.CONTENT_TOO_LARGE.message,
        )
    }
}

/** Use bodyStreamAsClass for body > 1MiB, bodyAsClass for body <= 1MiB. */
internal inline fun <reified T : Any> Context.bodyAsClassEfficient(): T {
    return if (req().contentLengthLong > SizeUnit.MB.multiplier) {
        bodyStreamAsClass<T>()
    } else {
        bodyAsClass<T>()
    }
}
