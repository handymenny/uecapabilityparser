package it.smartphonecombo.uecapabilityparser

import dev.adamko.kxstsgen.KxsTsGenerator
import it.smartphonecombo.uecapabilityparser.io.IOUtils
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.MultiCapabilities
import it.smartphonecombo.uecapabilityparser.model.index.LibraryIndex
import it.smartphonecombo.uecapabilityparser.query.Query
import it.smartphonecombo.uecapabilityparser.server.RequestCsv
import it.smartphonecombo.uecapabilityparser.server.RequestMultiPart
import it.smartphonecombo.uecapabilityparser.server.RequestParse
import it.smartphonecombo.uecapabilityparser.server.ServerStatus

internal object TsTypesGenerator {

    @JvmStatic
    fun main(args: Array<String>) {
        val tsGenerator = KxsTsGenerator()
        val warning =
            """
            |// Automatically generated. Don't edit.
            |// Run gradlew genTsTypes to update this file.
            |
            """
                .trimMargin()
        val typescriptDefinitions =
            tsGenerator.generate(
                Capabilities.serializer(),
                LibraryIndex.serializer(),
                MultiCapabilities.serializer(),
                ServerStatus.serializer(),
                RequestCsv.serializer(),
                RequestParse.serializer(),
                RequestMultiPart.serializer(),
                Query.serializer(),
            )
        val tsDefFixed =
            typescriptDefinitions.replace(" = \"\"", "INVALID = \"\"") // fix empty enum
        IOUtils.outputFileOrStdout(warning + tsDefFixed, "uecapabilityparser.d.ts")
        println("Typescript definitions exported to uecapabilityparser.d.ts")
    }
}
