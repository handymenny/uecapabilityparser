package it.smartphonecombo.uecapabilityparser.extension

import io.javalin.http.ContentType
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

internal fun Context.badRequest(message: String = "Bad Request") {
    result(message)
    status(HttpStatus.BAD_REQUEST_400)
}

internal fun Context.notFound(message: String = "Not Found") {
    result(message)
    status(HttpStatus.NOT_FOUND_404)
}

internal fun Context.internalError(message: String = "Internal Server Error") {
    result(message)
    status(HttpStatus.INTERNAL_SERVER_ERROR_500)
}

internal fun Context.attachFile(data: ByteArray, filename: String, contentType: ContentType) {
    result(data)
        .contentType(contentType)
        .header("Content-Disposition", "attachment; filename=$filename")
        .header("Access-Control-Expose-Headers", "Content-Disposition")
}
