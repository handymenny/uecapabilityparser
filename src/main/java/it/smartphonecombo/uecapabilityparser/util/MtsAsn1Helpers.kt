package it.smartphonecombo.uecapabilityparser.util

import com.ericsson.mts.asn1.ASN1Converter
import com.ericsson.mts.asn1.ASN1Lexer
import com.ericsson.mts.asn1.ASN1Parser
import com.ericsson.mts.asn1.ASN1Translator
import com.ericsson.mts.asn1.KotlinJsonFormatWriter
import com.ericsson.mts.asn1.PERTranslatorFactory
import com.ericsson.mts.asn1.converter.AbstractConverter
import it.smartphonecombo.uecapabilityparser.extension.decodeHex
import it.smartphonecombo.uecapabilityparser.extension.getArrayAtPath
import it.smartphonecombo.uecapabilityparser.extension.getString
import it.smartphonecombo.uecapabilityparser.extension.isLteUeCapInfoPayload
import it.smartphonecombo.uecapabilityparser.extension.isNrUeCapInfoPayload
import it.smartphonecombo.uecapabilityparser.extension.preformatHex
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.Rat
import java.io.InputStream
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.tree.ParseTree

object MtsAsn1Helpers {

    private val lteTrees: List<ParseTree> by
        lazy(LazyThreadSafetyMode.PUBLICATION) {
            parseTreeList(
                basePath = "/definition/lte-rrc/",
                "EUTRA-RRC-Definitions.asn",
                "EUTRA-InterNodeDefinitions.asn"
            )
        }

    private val nrTrees: List<ParseTree> by
        lazy(LazyThreadSafetyMode.PUBLICATION) {
            parseTreeList(
                basePath = "/definition/nr-rrc/",
                "NR-RRC-Definitions.asn",
                "NR-InterNodeDefinitions.asn"
            )
        }

    private val s1apTrees: List<ParseTree> by
        lazy(LazyThreadSafetyMode.PUBLICATION) {
            parseTreeList(
                basePath = "/definition/s1ap/",
                "S1AP-CommonDataTypes.asn",
                "S1AP-Constants.asn",
                "S1AP-Containers.asn",
                "S1AP-IEs.asn",
                "S1AP-PDU-Contents.asn",
                "S1AP-PDU-Descriptions.asn"
            )
        }

    private val ngapTrees: List<ParseTree> by
        lazy(LazyThreadSafetyMode.PUBLICATION) {
            parseTreeList(
                basePath = "/definition/ngap/",
                "NGAP-CommonDataTypes.asn",
                "NGAP-Constants.asn",
                "NGAP-Containers.asn",
                "NGAP-IEs.asn",
                "NGAP-PDU-Descriptions.asn",
                "NGAP-PDU-Contents.asn"
            )
        }

    fun getAsn1Converter(rat: Rat, converter: AbstractConverter): ASN1Converter {
        val trees = if (rat == Rat.EUTRA) lteTrees else nrTrees

        return ASN1Converter(converter, trees.first())
    }

    private fun getAsn1Translator(rat: Rat): ASN1Translator {
        val trees = if (rat == Rat.EUTRA) lteTrees else nrTrees

        return ASN1Translator.fromExternalTrees(PERTranslatorFactory(false), trees)
    }

    private fun getAsn1ApTranslator(rat: Rat): ASN1Translator {
        val trees = if (rat == Rat.EUTRA) s1apTrees else ngapTrees

        return ASN1Translator.fromExternalTrees(PERTranslatorFactory(true), trees)
    }

    fun apPDUtoJson(rat: Rat, pdu: ByteArray): JsonElement? {
        val idPdu = if (rat == Rat.EUTRA) "S1AP-PDU" else "NGAP-PDU"
        val jsonWriter = KotlinJsonFormatWriter()

        getAsn1ApTranslator(rat).decode(idPdu, pdu.inputStream(), jsonWriter)

        return jsonWriter.jsonNode
    }

    fun getRatListFromBytes(rrc: Rat, data: ByteArray): List<Rat> {
        if (data.isEmpty()) return emptyList()

        val ueCap = getRatContainersFromBytes(rrc, data)

        return ueCap?.mapNotNull { Rat.of(it.getString("rat-Type")) } ?: emptyList()
    }

    private fun getRatContainersFromBytes(rrc: Rat, data: ByteArray): JsonArray? {
        val jsonWriter = KotlinJsonFormatWriter()
        val translator = getAsn1Translator(rrc)

        val ratContainerListPath =
            if (rrc == Rat.EUTRA) {
                "message.c1.ueCapabilityInformation.criticalExtensions.c1.ueCapabilityInformation-r8.ue-CapabilityRAT-ContainerList"
            } else {
                "message.c1.ueCapabilityInformation.criticalExtensions.ueCapabilityInformation.ue-CapabilityRAT-ContainerList"
            }

        translator.decode("UL-DCCH-Message", data.inputStream(), jsonWriter)

        return jsonWriter.jsonNode?.getArrayAtPath(ratContainerListPath)
    }

    fun ratContainersFromRadioCapability(rrc: Rat, hexString: String): JsonArray? {
        val data = hexString.decodeHex().apply { if (isEmpty()) return null }

        val jsonWriter = KotlinJsonFormatWriter()
        val translator = getAsn1Translator(rrc)

        translator.decode("UERadioAccessCapabilityInformation", data.inputStream(), jsonWriter)

        val ratContainerListPath =
            if (rrc == Rat.NR) {
                "criticalExtensions.c1.ueRadioAccessCapabilityInformation.ue-RadioAccessCapabilityInfo"
            } else {
                "criticalExtensions.c1.ueRadioAccessCapabilityInformation-r8.ue-RadioAccessCapabilityInfo.criticalExtensions.c1.ueCapabilityInformation-r8.ue-CapabilityRAT-ContainerList"
            }

        return jsonWriter.jsonNode?.getArrayAtPath(ratContainerListPath)
    }

    fun getUeCapabilityJsonFromHex(defaultRat: Rat, hexInput: InputSource): JsonObject {
        val data =
            hexInput.readText().preformatHex().decodeHex().apply {
                if (isEmpty()) return buildJsonObject {}
            }

        val isLteCapInfo = data.isLteUeCapInfoPayload()
        val isNrCapInfo = data.isNrUeCapInfoPayload()

        if (!isLteCapInfo && !isNrCapInfo) {
            return ratContainerToJson(defaultRat, data)
        }

        val rrc: Rat
        val octetStringKey: String

        if (isLteCapInfo) {
            rrc = Rat.EUTRA
            octetStringKey = "ueCapabilityRAT-Container"
        } else {
            rrc = Rat.NR
            octetStringKey = "ue-CapabilityRAT-Container"
        }

        val ueCap = getRatContainersFromBytes(rrc, data) ?: JsonArray(emptyList())
        val map = mutableMapOf<String, JsonElement>()

        for (ueCapContainer in ueCap) {
            val ratType = Rat.of(ueCapContainer.getString("rat-Type"))
            val octetString = ueCapContainer.getString(octetStringKey)
            if (ratType != null && octetString != null) {
                map += ratContainerToJson(ratType, octetString.decodeHex())
            }
        }
        return JsonObject(map)
    }

    private fun getResourceAsStream(path: String): InputStream? =
        javaClass.getResourceAsStream(path)

    private fun ratContainerToJson(rat: Rat, bytes: ByteArray): JsonObject {
        if (rat != Rat.EUTRA && rat != Rat.EUTRA_NR && rat != Rat.NR) {
            return JsonObject(emptyMap())
        }

        val jsonWriter = KotlinJsonFormatWriter()
        val translator = getAsn1Translator(rat)

        return buildJsonObject {
            translator.decode(rat.ratCapabilityIdentifier, bytes.inputStream(), jsonWriter)
            jsonWriter.jsonNode?.let { put(rat.toString(), it) }
        }
    }

    private fun parseTreeList(basePath: String, vararg definitions: String): List<ParseTree> {
        return definitions.map { parseTree(getResourceAsStream("$basePath$it")!!) }
    }

    private fun parseTree(stream: InputStream): ParseTree {
        val inputStream = CharStreams.fromStream(stream)
        val asn1Lexer = ASN1Lexer(inputStream)
        val commonTokenStream = CommonTokenStream(asn1Lexer)
        val asn1Parser = ASN1Parser(commonTokenStream)
        asn1Parser.interpreter.predictionMode = PredictionMode.SLL
        return asn1Parser.moduleDefinition()
    }
}
