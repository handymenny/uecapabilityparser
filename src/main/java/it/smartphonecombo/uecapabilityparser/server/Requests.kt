package it.smartphonecombo.uecapabilityparser.server

import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.LogType
import it.smartphonecombo.uecapabilityparser.model.Rat
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
    data class LteCa(override val input: List<ComboLte>, val newCsvFormat: Boolean = false) :
        RequestCsv

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

class RequestParse(
    val input: InputSource? = null,
    val inputNR: InputSource? = null,
    val inputENDC: InputSource? = null,
    val type: LogType,
    val description: String = "",
) {
    companion object {
        fun buildRequest(
            vararg inputs: InputSource,
            type: LogType,
            description: String,
            ratList: List<Rat>,
        ): RequestParse {
            val inputIndex = ratList.indexOf(Rat.EUTRA)
            val inputNrIndex = ratList.indexOf(Rat.NR)
            val inputEnDcIndex = ratList.indexOf(Rat.EUTRA_NR)

            val input = inputs.getOrNull(inputIndex)
            val inputNR = inputs.getOrNull(inputNrIndex)
            val inputENDC = inputs.getOrNull(inputEnDcIndex)

            require(ratList.size >= inputs.size) { "Something weird, inputs list >= rat List" }

            return RequestParse(input, inputNR, inputENDC, type, description)
        }
    }
}

@Serializable
class RequestMultiPart(
    val inputIndexes: List<Int>,
    val type: LogType,
    val subTypes: List<String> = emptyList(),
    val description: String = "",
)
