package it.smartphonecombo.uecapabilityparser

import it.smartphonecombo.uecapabilityparser.importer.multi.ImportScat
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.math.abs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody

object UtilityForTests {

    internal fun Path.listFilesRecursively(): List<File>? {
        return Files.walk(this)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .collect(Collectors.toList())
    }

    /** Check if the given directories have the same tree and if files have similar sizes (± 3%) */
    internal fun dirsSimilar(expected: String, actual: String): Boolean {
        val pathA = Path(expected)
        val pathB = Path(actual)
        val dirA = pathA.toFile()
        val dirB = pathB.toFile()
        val filesA = pathA.listFilesRecursively()
        val filesB = pathB.listFilesRecursively()

        if (filesA == null && filesB == null) {
            return true
        } else if (filesA == null || filesB == null) {
            return false
        } else if (filesA.size != filesB.size) {
            return false
        }

        val zip = filesA.zip(filesB)

        val result =
            zip.all { (fileA, fileB) ->
                if (fileA.relativeTo(dirA) != fileB.relativeTo(dirB)) {
                    false
                } else if (fileA.isDirectory && fileB.isDirectory) {
                    true
                } else {
                    val aSize = fileA.readBytes().size
                    val bSize = fileB.readBytes().size
                    abs(aSize - bSize) < 3 * aSize / 100
                }
            }
        return result
    }

    internal fun multiPartRequest(url: String, json: JsonElement, files: List<String>): Request {

        val bodyBuilder =
            MultipartBody.Builder().apply {
                setType(MultipartBody.FORM)
                addFormDataPart("requests", Json.encodeToString(json))
                files.forEach {
                    val file = File(it)
                    addFormDataPart("file", file.name, file.asRequestBody())
                }
            }
        val reqBuilder =
            Request.Builder().apply {
                url(url)
                post(bodyBuilder.build())
            }
        return reqBuilder.build()
    }

    val scatAvailable = ImportScat.isScatAvailable()
}
