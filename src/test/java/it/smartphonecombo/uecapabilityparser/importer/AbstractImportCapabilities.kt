package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests.RECREATE_ORACLES
import it.smartphonecombo.uecapabilityparser.UtilityForTests.recreateCapabilitiesOracles
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions

abstract class AbstractImportCapabilities(
    private val parser: ImportCapabilities,
    private val path: String,
    private val oracleSubDir: String = "oracle",
) {

    protected fun parse(inputFilename: String, oracleFilename: String) {
        val filePath = "$path/input/$inputFilename"
        val oraclePath = "$path/$oracleSubDir/$oracleFilename"

        val actual = parser.parse(File(filePath).toInputSource())

        if (RECREATE_ORACLES) recreateCapabilitiesOracles(oraclePath, actual)

        val expected = Json.decodeFromString<Capabilities>(File(oraclePath).readText())

        Assertions.assertEquals(expected, actual)
    }
}
