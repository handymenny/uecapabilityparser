package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.band.BandBoxed
import it.smartphonecombo.uecapabilityparser.model.band.BandLteDetails
import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.filter.IUeCapabilityFilter
import it.smartphonecombo.uecapabilityparser.util.Config
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
    @SerialName("nrNsaBandsEutra") var nrNSAbands: List<BandBoxed> = emptyList(),
    @SerialName("nrSaBandsEutra") var nrSAbands: List<BandBoxed> = emptyList(),
    @SerialName("nrBands") var nrBands: List<BandNrDetails> = emptyList(),
    @SerialName("lteCategoryDl") var lteCategoryDL: Int = 0,
    @SerialName("lteCategoryUl") var lteCategoryUL: Int = 0,
    @SerialName("altTbsIndexes") var altTbsIndexes: List<String> = emptyList(),
    @SerialName("endc") var enDcCombos: List<ComboEnDc> = emptyList(),
    @SerialName("nrca") var nrCombos: List<ComboNr> = emptyList(),
    @SerialName("nrdc") var nrDcCombos: List<ComboNrDc> = emptyList(),
    @Required @SerialName("logType") var logType: LogType = LogType.INVALID,
    @SerialName("ueCapFilters") var ueCapFilters: List<IUeCapabilityFilter> = emptyList(),
    @Required @SerialName("metadata") val metadata: MutableMap<String, String> = mutableMapOf()
) {
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @SerialName("id")
    var id: String = UUID.randomUUID().toString()

    @Required
    @SerialName("parserVersion")
    var parserVersion: String = Config.getOrDefault("project.version", "")

    @Required @SerialName("timestamp") var timestamp: Long = 0

    fun addMetadata(key: String, value: Any) {
        if (key in metadata) {
            metadata[key] += ", $value"
        } else {
            metadata[key] = "$value"
        }
    }

    fun setMetadata(key: String, value: Any) {
        metadata[key] = value.toString()
    }

    fun getStringMetadata(key: String): String? {
        return metadata.getOrDefault(key, null)
    }
}
