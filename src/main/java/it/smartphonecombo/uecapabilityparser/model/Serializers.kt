package it.smartphonecombo.uecapabilityparser.model

import java.util.Base64
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ByteArrayBase64Serializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ByteArrayBase64", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray) {
        val string = Base64.getEncoder().encodeToString(value)
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        val string = decoder.decodeString()
        return Base64.getDecoder().decode(string)
    }
}
