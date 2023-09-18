package it.smartphonecombo.uecapabilityparser

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import it.smartphonecombo.uecapabilityparser.model.band.BandBoxed
import it.smartphonecombo.uecapabilityparser.model.filter.BandFilterLte
import it.smartphonecombo.uecapabilityparser.model.filter.BandFilterNr
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.math.abs

object UtilityForTests {

    internal fun getResourceAsStream(path: String): InputStream? =
        object {}.javaClass.getResourceAsStream(path)

    internal fun Number.toPowerClass(): PowerClass {
        return when (this) {
            1 -> PowerClass.PC1
            1.5 -> PowerClass.PC1dot5
            2 -> PowerClass.PC2
            3 -> PowerClass.PC3
            4 -> PowerClass.PC4
            5 -> PowerClass.PC5
            6 -> PowerClass.PC6
            7 -> PowerClass.PC7
            else -> PowerClass.NONE
        }
    }

    internal fun Band.toBandBoxed(): BandBoxed {
        return BandBoxed(this)
    }

    internal fun Band.toBandFilterLte(): BandFilterLte {
        return BandFilterLte(this)
    }

    internal fun Band.toBandFilterNr(): BandFilterNr {
        return BandFilterNr(this)
    }

    internal fun String.toBwClass(): BwClass {
        return BwClass.valueOf(this)
    }

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
}
