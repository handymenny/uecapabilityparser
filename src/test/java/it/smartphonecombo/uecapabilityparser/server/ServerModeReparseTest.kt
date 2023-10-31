package it.smartphonecombo.uecapabilityparser.server

import it.smartphonecombo.uecapabilityparser.UtilityForTests.dirsSimilar
import it.smartphonecombo.uecapabilityparser.cli.Main
import it.smartphonecombo.uecapabilityparser.util.Config
import it.smartphonecombo.uecapabilityparser.util.IO
import it.smartphonecombo.uecapabilityparser.util.Property
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ServerModeReparseTest {
    private val resourcesPath = "src/test/resources/server"
    private val tmpStorePath = UUID.randomUUID().toString() + "-tmp"
    private val parserVersion = Property.getProperty("project.version") ?: ""

    @BeforeEach
    fun setup() {
        try {
            deleteDirectory(tmpStorePath)
        } catch (_: Exception) {}
        try {
            createMultiDir("$resourcesPath/oracleForReparse")
            copyDirectory("$resourcesPath/inputForReparse", tmpStorePath)
            replaceVersion(tmpStorePath, "staging", parserVersion)
        } catch (_: Exception) {}
    }

    @AfterEach
    fun teardown() {
        try {
            deleteDirectory(tmpStorePath)
        } catch (_: Exception) {}
        Config.clear()
    }

    @Test
    fun testReparseAuto() {
        test(
            arrayOf("server", "-p", "0", "--store", tmpStorePath, "--reparse", "auto"),
            "$resourcesPath/oracleForReparse/auto"
        )
    }

    @Test
    fun testReparseAutoCompression() {
        test(
            arrayOf(
                "server",
                "-p",
                "0",
                "--store",
                tmpStorePath,
                "--reparse",
                "auto",
                "--compression"
            ),
            "$resourcesPath/oracleForReparse/autoCompress"
        )
    }

    @Test
    fun testReparseForce() {
        test(
            arrayOf("server", "-p", "0", "--store", tmpStorePath, "--reparse", "force"),
            "$resourcesPath/oracleForReparse/force"
        )
    }

    @Test
    fun testReparseForceCompression() {
        test(
            arrayOf(
                "server",
                "-p",
                "0",
                "--store",
                tmpStorePath,
                "--reparse",
                "force",
                "--compression"
            ),
            "$resourcesPath/oracleForReparse/forceCompress"
        )
    }

    fun test(args: Array<String>, oraclePath: String) {
        Main.main(args)
        assertTrue(dirsSimilar(oraclePath, tmpStorePath))
    }

    private fun deleteDirectory(path: String) {
        return Files.walk(Path.of(path))
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete)
    }

    private fun copyDirectory(src: String, dst: String) {
        val srcFile = File(src)
        val dstFile = File(dst)

        srcFile.copyRecursively(dstFile)
    }

    private fun createMultiDir(path: String) {
        File(path).listFiles()?.filter(File::isDirectory)?.forEach {
            IO.createDirectories(it.absolutePath + "/multi")
        }
    }

    private fun replaceVersion(directory: String, search: String, replace: String) {
        Files.walk((Path.of(directory)))
            .filter { it.name.endsWith(".json") || it.name.endsWith(".json.gz") }
            .forEach { path ->
                val compression = path.extension == "gz"
                val text = IO.readTextFromFile(path.toFile(), compression)
                text?.let {
                    val newText = it.replace(search, replace)
                    IO.outputFile(
                        newText.toByteArray(),
                        path.toString().substringBeforeLast(".gz"),
                        compression
                    )
                }
            }
    }
}
