package it.smartphonecombo.uecapabilityparser.model.index

import it.smartphonecombo.uecapabilityparser.extension.custom
import it.smartphonecombo.uecapabilityparser.util.Config
import java.io.File
import java.time.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.DecodeSequenceMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence

class ReparsingIndex {
    private val map = mutableMapOf<String, ReparsingIndexLine>()
    private val lock = Any()

    operator fun get(id: String) = map[id]

    fun put(line: ReparsingIndexLine) = synchronized(lock) { map.put(line.id, line) }

    fun putAll(lines: List<ReparsingIndexLine>) =
        synchronized(lock) { map.putAll(lines.associateBy { it.id }) }

    fun getAll(): List<ReparsingIndexLine> = synchronized(lock) { map.values.toList() }

    fun store(path: String) {
        try {
            val list = getAll().sortedBy { it.id }
            File(path).bufferedWriter().use { out ->
                list.forEach {
                    val str = Json.custom().encodeToString(it)
                    out.write(str)
                    out.newLine()
                }
            }
        } catch (_: Exception) {}
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun fromFile(file: String): ReparsingIndex {
            val new = ReparsingIndex()
            try {
                val inputStream = File(file).inputStream()
                val list =
                    Json.custom()
                        .decodeToSequence<ReparsingIndexLine>(
                            inputStream,
                            DecodeSequenceMode.WHITESPACE_SEPARATED,
                        )
                new.putAll(list.toList())
            } catch (_: Exception) {}
            return new
        }
    }
}

@Serializable
data class ReparsingIndexLine(
    val id: String,
    @Required val timestamp: Long = Instant.now().toEpochMilli(),
    @Required val parserVersion: String = Config.getOrDefault("project.version", ""),
    val error: String? = null,
)
