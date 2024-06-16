package it.smartphonecombo.uecapabilityparser.io

import it.smartphonecombo.uecapabilityparser.extension.toInputSource
import java.util.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InputSourceBase64Serializer : KSerializer<InputSource> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ByteArrayBase64", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: InputSource) {
        val string = Base64.getEncoder().encodeToString(value.readBytes())
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): InputSource {
        val string = decoder.decodeString()
        return Base64.getDecoder().decode(string).toInputSource()
    }
}

object BwClassSerializer : KSerializer<Char> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BwClass", PrimitiveKind.CHAR)

    override fun serialize(encoder: Encoder, value: Char) = encoder.encodeChar(value)

    override fun deserialize(decoder: Decoder): Char = decoder.decodeChar().uppercaseChar()
}
