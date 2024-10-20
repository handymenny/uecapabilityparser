package it.smartphonecombo.uecapabilityparser.server

import io.mockk.every
import io.mockk.mockkStatic
import it.smartphonecombo.uecapabilityparser.UtilityForTests.dirsSimilar
import it.smartphonecombo.uecapabilityparser.cli.Main
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ServerModeReparseTest {
    private val resourcesPath = "src/test/resources/server"
    private val tmpStorePath = UUID.randomUUID().toString() + "-tmp"
    private val parserVersion = Config.getOrDefault("project.version", "")

    @BeforeEach
    fun setup() {
        try {
            deleteDirectory(tmpStorePath)
        } catch (_: Exception) {}
        try {
            createMultiDir("$resourcesPath/oracleForReparse")
            copyDirectory("$resourcesPath/inputForReparse", tmpStorePath)
            replaceVersion("$tmpStorePath/good", "staging", parserVersion)
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
            arrayOf("server", "-p", "0", "--store", "$tmpStorePath/good", "--reparse", "auto"),
            "$resourcesPath/oracleForReparse/auto",
            "$tmpStorePath/good",
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
                "$tmpStorePath/good",
                "--reparse",
                "auto",
                "--compression",
            ),
            "$resourcesPath/oracleForReparse/autoCompress",
            "$tmpStorePath/good",
        )
    }

    @Test
    fun testReparseForce() {
        test(
            arrayOf("server", "-p", "0", "--store", "$tmpStorePath/good", "--reparse", "force"),
            "$resourcesPath/oracleForReparse/force",
            "$tmpStorePath/good",
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
                "$tmpStorePath/good",
                "--reparse",
                "force",
                "--compression",
            ),
            "$resourcesPath/oracleForReparse/forceCompress",
            "$tmpStorePath/good",
        )
    }

    @Test
    fun testReparseForceBad() {
        test(
            arrayOf(
                "server",
                "-p",
                "0",
                "--store",
                "$tmpStorePath/bad",
                "--reparse",
                "force",
                "--compression",
            ),
            "$resourcesPath/oracleForReparse/forceBad",
            "$tmpStorePath/bad",
        )
    }

    fun test(args: Array<String>, storePath: String, oraclePath: String) {
        Main.main(args)
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(dirsSimilar(oraclePath, storePath))
    }

    private fun deleteDirectory(path: String) {
        return Files.walk(Paths.get(path))
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
            IOUtils.createDirectories(it.absolutePath + "/multi")
        }
    }

    private fun replaceVersion(directory: String, search: String, replace: String) {
        Files.walk((Paths.get(directory)))
            .filter { it.name.endsWith(".json") || it.name.endsWith(".json.gz") }
            .forEach { path ->
                val compression = path.extension == "gz"
                val text = path.toFile().toInputSource(compression).readText()
                val newText = text.replace(search, replace)
                IOUtils.outputFile(
                    newText.toByteArray(),
                    path.toString().substringBeforeLast(".gz"),
                    compression,
                )
            }
    }

    companion object {
        private val dispatcher = StandardTestDispatcher()

        @JvmStatic
        @BeforeAll
        fun mockDispatchers() {
            mockkStatic(Dispatchers::class)
            every { Dispatchers.IO } returns dispatcher
        }
    }
}
