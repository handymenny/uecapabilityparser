package it.smartphonecombo.uecapabilityparser

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.math.abs

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

    internal fun scatIsAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("scat", "-h"))
            process.waitFor()
            process.exitValue() == 0
        } catch (ignored: Exception) {
            false
        }
    }
}
