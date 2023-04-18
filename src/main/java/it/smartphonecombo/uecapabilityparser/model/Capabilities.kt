package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte

data class Capabilities(
    var lteCombos: List<ComboLte> = emptyList(),
    var lteBands: List<ComponentLte> = emptyList(),
    var nrNSAbands: List<BandNrDetails> = emptyList(),
    var nrSAbands: List<BandNrDetails> = emptyList(),
    var nrBands: List<BandNrDetails> = emptyList(),
    var lteCategoryDL: Int = 0,
    var lteCategoryUL: Int = 0,
    var enDcCombos: List<ComboEnDc> = emptyList(),
    var nrCombos: List<ComboNr> = emptyList(),
    var nrDcCombos: List<ComboNrDc> = emptyList(),
    val metadata: MutableMap<String, String> = mutableMapOf()
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
