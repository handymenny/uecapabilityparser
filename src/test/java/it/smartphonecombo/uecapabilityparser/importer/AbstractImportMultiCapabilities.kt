package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.UtilityForTests.RECREATE_ORACLES
import it.smartphonecombo.uecapabilityparser.UtilityForTests.recreateCapabilitiesListOracles
import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportMultiCapabilities
import it.smartphonecombo.uecapabilityparser.importer.multi.ImportScat
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.LogType
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions

abstract class AbstractImportMultiCapabilities(
    private val parser: ImportMultiCapabilities,
    private val path: String,
    private val oracleSubDir: String = "oracle",
) {

    fun parse(inputFilename: String, oracleFilename: String, logType: LogType = LogType.INVALID) {
        val filePath = "$path/input/$inputFilename"
        val oraclePath = "$path/$oracleSubDir/$oracleFilename"

        val input = File(filePath).toInputSource()
        val multi =
            if (parser !is ImportScat) {
                parser.parse(input)
            } else {
                parser.parse(input, logType)
            }

        val actual = multi?.parsingList?.map { it.capabilities }!!

        if (RECREATE_ORACLES) recreateCapabilitiesListOracles(oraclePath, actual)

        val expected = Json.decodeFromString<List<Capabilities>>(File(oraclePath).readText())

        // Check size
        Assertions.assertEquals(expected.size, actual.size)

        // override dynamic properties
        for (i in expected.indices) {
            val capA = actual[i]
            val capE = expected[i]

            capA.getStringMetadata("processingTime")?.let { capE.setMetadata("processingTime", it) }
        }

        Assertions.assertEquals(expected, actual)
    }
}
