package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.band.BandLteDetails
import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.util.Property
import java.util.UUID
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Capabilities(
    @SerialName("lteca") var lteCombos: List<ComboLte> = emptyList(),
    @SerialName("lteBands") var lteBands: List<BandLteDetails> = emptyList(),
    @SerialName("nrNsaBandsEutra") var nrNSAbands: List<BandNrDetails> = emptyList(),
    @SerialName("nrSaBandsEutra") var nrSAbands: List<BandNrDetails> = emptyList(),
    @SerialName("nrBands") var nrBands: List<BandNrDetails> = emptyList(),
    @SerialName("lteCategoryDl") var lteCategoryDL: Int = 0,
    @SerialName("lteCategoryUl") var lteCategoryUL: Int = 0,
    @SerialName("endc") var enDcCombos: List<ComboEnDc> = emptyList(),
    @SerialName("nrca") var nrCombos: List<ComboNr> = emptyList(),
    @SerialName("nrdc") var nrDcCombos: List<ComboNrDc> = emptyList(),
    @Required @SerialName("logType") var logType: String = "",
    @Required
    @SerialName("parserVersion")
    var parserVersion: String = Property.getProperty("project.version") ?: "",
    @Required @SerialName("timestamp") var timestamp: Long = 0,
    @Required @SerialName("metadata") val metadata: MutableMap<String, String> = mutableMapOf(),
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @SerialName("id")
    var id: String = UUID.randomUUID().toString()
) {
    fun setMetadata(key: String, value: Any) {
        metadata[key] = value.toString()
    }

    fun getStringMetadata(key: String): String? {
        return metadata.getOrDefault(key, null)
    }

    fun getIntMetadata(key: String): Int? {
        return metadata.getOrDefault(key, null)?.toIntOrNull()
    }
}
