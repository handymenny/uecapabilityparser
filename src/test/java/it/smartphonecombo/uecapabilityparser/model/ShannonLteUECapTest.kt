@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.shannon.lte.ShannonLteUECap
import java.io.File
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ShannonLteUECapTest {

    private val resourcesPath = "src/test/resources/shannon/input"
    private val oracleJsonPath = "src/test/resources/shannon/oracle"

    @Test
    fun testToJsonLte() {
        protobufToJson("$resourcesPath/lte.binarypb", "$oracleJsonPath/lte.json")
    }

    @Test
    fun testToJsonLte2() {
        protobufToJson("$resourcesPath/lte2.binarypb", "$oracleJsonPath/lte2.json")
    }

    @Test
    fun testReEncodeLte() {
        reEncodeProtobuf("$resourcesPath/lte.binarypb")
    }

    @Test
    fun testReEncodeLte2() {
        reEncodeProtobuf("$resourcesPath/lte2.binarypb")
    }

    private fun protobufToJson(inputPath: String, oraclePath: String) {
        val inputBinary = File(inputPath).readBytes()
        val ueCap = ProtoBuf.decodeFromByteArray<ShannonLteUECap>(inputBinary)

        val oracleText = File(oraclePath).readText()
        val oracleObject = Json.decodeFromString<ShannonLteUECap>(oracleText)

        assertEquals(oracleObject, ueCap)
    }

    private fun reEncodeProtobuf(inputPath: String) {
        val inputBinary = File(inputPath).readBytes()
        val ueCap = ProtoBuf.decodeFromByteArray<ShannonLteUECap>(inputBinary)

        val reEncodedBinary = ProtoBuf.encodeToByteArray<ShannonLteUECap>(ueCap)

        assertArrayEquals(inputBinary, reEncodedBinary)
    }
}
