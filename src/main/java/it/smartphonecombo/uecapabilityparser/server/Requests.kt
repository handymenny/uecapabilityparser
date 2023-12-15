package it.smartphonecombo.uecapabilityparser.server

import it.smartphonecombo.uecapabilityparser.model.ByteArrayBase64Serializer
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RequestCsv {
    val input: List<ICombo>
    val type
        get() = javaClass.simpleName.lowercase()

    @Serializable
    @SerialName("lteca")
    data class LteCa(override val input: List<ComboLte>) : RequestCsv

    @Serializable
    @SerialName("nrca")
    data class NrCa(override val input: List<ComboNr>) : RequestCsv

    @Serializable
    @SerialName("nrdc")
    data class NrDc(override val input: List<ComboNrDc>) : RequestCsv

    @Serializable
    @SerialName("endc")
    data class EnDc(override val input: List<ComboEnDc>) : RequestCsv
}

@Serializable
class RequestParse(
    @Serializable(with = ByteArrayBase64Serializer::class) val input: ByteArray? = null,
    @Serializable(with = ByteArrayBase64Serializer::class) val inputNR: ByteArray? = null,
    @Serializable(with = ByteArrayBase64Serializer::class) val inputENDC: ByteArray? = null,
    val defaultNR: Boolean = false,
    val type: LogType,
    val description: String = ""
) {
    companion object {
        fun buildRequest(
            vararg inputs: ByteArray,
            type: LogType,
            description: String,
            defaultNR: Boolean
        ): RequestParse {
            val input = inputs.firstOrNull()
            val firstInputIsNr = type == LogType.H && defaultNR
            val inputNR = if (firstInputIsNr) null else inputs.getOrNull(1)
            val inputENDC = if (firstInputIsNr) inputs.getOrNull(1) else inputs.getOrNull(2)

            return RequestParse(input, inputNR, inputENDC, defaultNR, type, description)
        }
    }
}
