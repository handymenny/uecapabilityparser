package it.smartphonecombo.uecapabilityparser.io

import it.smartphonecombo.uecapabilityparser.extension.decodeHex
import it.smartphonecombo.uecapabilityparser.model.ByteArrayDeepEquals
import it.smartphonecombo.uecapabilityparser.model.index.IndexLine
import it.smartphonecombo.uecapabilityparser.model.index.MultiIndexLine
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object BwClassSerializer : KSerializer<Char> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BwClass", PrimitiveKind.CHAR)

    override fun serialize(encoder: Encoder, value: Char) = encoder.encodeChar(value)

    override fun deserialize(decoder: Decoder): Char = decoder.decodeChar().uppercaseChar()
}

object HexSerializer : KSerializer<ByteArrayDeepEquals> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("HexString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArrayDeepEquals) {
        throw NotImplementedError("Serialization not implemented.")
    }

    override fun deserialize(decoder: Decoder): ByteArrayDeepEquals {
        val string = decoder.decodeString()
        return ByteArrayDeepEquals(string.decodeHex())
    }
}

object DateTimeSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DateTimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Long) {
        throw NotImplementedError("Serialization not implemented.")
    }

    override fun deserialize(decoder: Decoder): Long {
        val string = decoder.decodeString()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return Instant.from(formatter.parse(string)).toEpochMilli()
    }
}

sealed class MapAsListSerializer<K : Any, V : Any>(dataSerializer: KSerializer<V>) :
    KSerializer<MutableMap<K, V>> {
    protected val delegateSerializer = ListSerializer(dataSerializer)
    protected abstract val associateFunction: (V) -> K
    abstract override val descriptor: SerialDescriptor

    override fun serialize(encoder: Encoder, value: MutableMap<K, V>) {
        val data = value.values.toList()
        encoder.encodeSerializableValue(delegateSerializer, data)
    }

    override fun deserialize(decoder: Decoder): MutableMap<K, V> {
        val list = decoder.decodeSerializableValue(delegateSerializer)

        return list.associateBy(associateFunction).toMutableMap()
    }
}

object IndexLineMapAsList : MapAsListSerializer<String, IndexLine>(IndexLine.serializer()) {
    override val associateFunction: (IndexLine) -> String = { it.id }

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("IndexLineList", delegateSerializer.descriptor)
}

object MultiIndexLineMapAsList :
    MapAsListSerializer<String, MultiIndexLine>(MultiIndexLine.serializer()) {
    override val associateFunction: (MultiIndexLine) -> String = { it.id }

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("MultiIndexLineList", delegateSerializer.descriptor)
}
