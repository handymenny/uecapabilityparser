@file:OptIn(ExperimentalSerializationApi::class)

package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.shannon.ShannonNrUECap
import it.smartphonecombo.uecapabilityparser.util.IO
import java.lang.RuntimeException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ShannonNrUECapTest {

    private val resourcesPath = "src/test/resources/shannon/input"
    private val oracleJsonPath = "src/test/resources/shannon/oracle"

    @Test
    fun testToJsonEmpty() {
        protobufToJson("$resourcesPath/empty.binarypb", "$oracleJsonPath/empty.json")
    }

    @Test
    fun testToJsonSub6() {
        protobufToJson("$resourcesPath/sub6.binarypb", "$oracleJsonPath/sub6.json")
    }

    @Test
    fun testToJsonSub62() {
        protobufToJson("$resourcesPath/sub6_2.binarypb", "$oracleJsonPath/sub6_2.json")
    }

    @Test
    fun testToJsonMmWave() {
        protobufToJson("$resourcesPath/mmWave.binarypb", "$oracleJsonPath/mmWave.json")
    }

    @Test
    fun testToJsonMmWave2() {
        protobufToJson("$resourcesPath/mmWave_2.binarypb", "$oracleJsonPath/mmWave_2.json")
    }

    @Test
    fun testReEncodeEmpty() {
        reEncodeProtobuf("$resourcesPath/empty.binarypb")
    }

    @Test
    fun testReEncodeSub6() {
        reEncodeProtobuf("$resourcesPath/sub6.binarypb")
    }

    @Test
    fun testReEncodeSub62() {
        reEncodeProtobuf("$resourcesPath/sub6_2.binarypb")
    }

    @Test
    fun testReEncodeMmWave() {
        reEncodeProtobuf("$resourcesPath/mmWave.binarypb")
    }

    @Test
    fun testReEncodeMmWave2() {
        reEncodeProtobuf("$resourcesPath/mmWave_2.binarypb")
    }

    private fun protobufToJson(inputPath: String, oraclePath: String) {
        val json = Json { prettyPrint = true }
        val inputBinary =
            IO.readBytesFromFile(inputPath, false) ?: throw RuntimeException("Error reading input")
        val nrUECap = ProtoBuf.decodeFromByteArray<ShannonNrUECap>(inputBinary)

        val oracleText =
            IO.readTextFromFile(oraclePath, false) ?: throw RuntimeException("Error reading oracle")
        val oracleObject = json.decodeFromString<ShannonNrUECap>(oracleText)

        assertEquals(oracleObject, nrUECap)
    }

    private fun reEncodeProtobuf(inputPath: String) {
        val inputBinary =
            IO.readBytesFromFile(inputPath, false) ?: throw RuntimeException("Error reading input")
        val nrUECap = ProtoBuf.decodeFromByteArray<ShannonNrUECap>(inputBinary)

        val reEncodedBinary = ProtoBuf.encodeToByteArray<ShannonNrUECap>(nrUECap)

        assertArrayEquals(inputBinary, reEncodedBinary)
    }
}
