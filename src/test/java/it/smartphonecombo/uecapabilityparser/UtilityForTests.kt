package it.smartphonecombo.uecapabilityparser

import io.javalin.testtools.Request
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportScat
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import java.io.File
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpRequest.BodyPublishers
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.math.abs
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.Assertions

object UtilityForTests {

    internal fun Path.listFilesRecursively(): List<File>? {
        return Files.walk(this)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .collect(Collectors.toList())
    }

    /** Check if the given directories have the same tree and if files have similar sizes (± 5%) */
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
                    abs(aSize - bSize) < maxOf(3 * aSize / 100, 128)
                }
            }
        return result
    }

    internal fun multiPartRequest(
        url: String,
        json: JsonElement,
        files: List<String>,
        gzip: Boolean = false,
    ): Request {
        val boundary = "Boundary-${UUID.randomUUID()}"
        val crlf = "\r\n"

        fun partHeader(vararg headers: String): BodyPublisher =
            BodyPublishers.ofString(
                headers
                    .joinToString(separator = crlf, postfix = "$crlf$crlf") { it }
                    .let { "--$boundary$crlf$it" },
                StandardCharsets.UTF_8,
            )

        val parts = buildList {
            add(
                partHeader(
                    "Content-Disposition: form-data; name=\"requests\"",
                    "Content-Type: text/plain; charset=UTF-8",
                )
            )
            add(BodyPublishers.ofString(Json.encodeToString(json) + crlf, StandardCharsets.UTF_8))

            files.forEach { path ->
                val file = File(path)
                add(
                    partHeader(
                        """Content-Disposition: form-data; name="file"; filename="${file.name}"""",
                        "Content-Type: application/octet-stream",
                    )
                )
                add(BodyPublishers.ofByteArray(file.toInputSource(gzip).readBytes()))
                add(BodyPublishers.ofString(crlf, StandardCharsets.UTF_8))
            }

            add(BodyPublishers.ofString("--$boundary--$crlf", StandardCharsets.UTF_8))
        }

        return Request.Builder()
            .url(url)
            .header("Content-Type", "multipart/form-data; boundary=$boundary")
            .post(BodyPublishers.concat(*parts.toTypedArray()))
            .build()
    }

    internal fun capabilitiesAssertEquals(
        expectedPathSingle: String,
        actual: String,
        actualIsMulti: Boolean = false,
    ): Capabilities {
        val actualCap =
            if (actualIsMulti) {
                Json.decodeFromString<MultiCapabilities>(actual).capabilities.first()
            } else {
                Json.decodeFromString<Capabilities>(actual)
            }

        if (RECREATE_ORACLES)
            recreateCapabilitiesOracles(
                expectedPathSingle,
                actualCap,
                prettyPrint = false,
                preserveMetadata = true,
            )

        val expectedInputSource =
            File(expectedPathSingle).toInputSource(gzip = expectedPathSingle.endsWith(".gz"))
        val expectedCap = Json.decodeFromString<Capabilities>(expectedInputSource.readText())

        // Override dynamic properties
        actualCap.getStringMetadata("processingTime")?.let {
            expectedCap.setMetadata("processingTime", it)
        }

        Assertions.assertEquals(expectedCap, actualCap)
        return actualCap
    }

    internal fun recreateCapabilitiesOracles(
        oraclePath: String,
        cap: Capabilities,
        prettyPrint: Boolean = true,
        preserveMetadata: Boolean = false,
    ) {
        val json = if (prettyPrint) jsonPrettyPrint else Json
        val isGz = oraclePath.endsWith(".gz")

        val clonedCap = cap.copy()

        if (!preserveMetadata) {
            // reset id, timestamp, processingTime
            clonedCap.id = ""
            clonedCap.timestamp = 0
            clonedCap.metadata.remove("processingTime")
        } else {
            // copy current id, timestamp, processingTime
            val inputSource = File(oraclePath).toInputSource(gzip = isGz)
            val prev = json.decodeFromString<Capabilities>(inputSource.readText())
            clonedCap.id = prev.id
            clonedCap.timestamp = prev.timestamp
            prev.getStringMetadata("processingTime")?.let {
                clonedCap.setMetadata("processingTime", it)
            }
        }

        val string = json.encodeToString(clonedCap) + "\n"

        val outputFilePath = oraclePath.removeSuffix(".gz")

        IOUtils.outputFile(string.toByteArray(), outputFilePath, isGz)
    }

    internal fun recreateCapabilitiesListOracles(oraclePath: String, capList: List<Capabilities>) {
        // reset id, timestamp, processingTime
        for (cap in capList) {
            cap.id = ""
            cap.timestamp = 0
            cap.metadata.remove("processingTime")
        }

        val string = jsonPrettyPrint.encodeToString(capList) + "\n"

        IOUtils.outputFileOrStdout(string, oraclePath)
    }

    internal fun recreateMultiCapabilitiesOracles(
        oraclePath: String,
        multiCap: MultiCapabilities,
        prettyPrint: Boolean = true,
        preserveMetadata: Boolean = false,
    ) {
        val json = if (prettyPrint) jsonPrettyPrint else Json
        val prevMultiCap = json.decodeFromString<MultiCapabilities>(File(oraclePath).readText())
        val prevCapsIterator = prevMultiCap.capabilities.listIterator()

        val clonedCaps =
            multiCap.capabilities.map {
                val clone = it.copy()
                if (!preserveMetadata) {
                    // reset id, timestamp, processingTime
                    clone.id = ""
                    clone.timestamp = 0
                    clone.metadata.remove("processingTime")
                } else {
                    val prevCap = prevCapsIterator.next()
                    clone.id = prevCap.id
                    clone.timestamp = prevCap.timestamp
                    prevCap.getStringMetadata("processingTime")?.let {
                        clone.setMetadata("processingTime", it)
                    }
                }
                clone
            }

        val multiCloned = multiCap.copy(clonedCaps)

        if (!preserveMetadata) {
            multiCloned.id = ""
        } else {
            multiCloned.id = prevMultiCap.id
        }

        val string = json.encodeToString(multiCloned) + "\n"

        IOUtils.outputFileOrStdout(string, oraclePath)
    }

    internal fun recreateDirOracles(oraclePath: String, actualPath: String) {
        deleteDirectory(oraclePath)
        copyDirectory(actualPath, oraclePath)
    }

    internal fun deleteDirectory(path: String) {
        return Files.walk(Paths.get(path))
            .sorted(java.util.Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
    }

    internal fun copyDirectory(src: String, dst: String) {
        val srcFile = File(src)
        val dstFile = File(dst)

        srcFile.copyRecursively(dstFile)
    }

    // not supported by all test classes
    const val RECREATE_ORACLES = false
    val scatAvailable = ImportScat.isScatAvailable() == 1
    private val jsonPrettyPrint = Json { prettyPrint = true }
}
