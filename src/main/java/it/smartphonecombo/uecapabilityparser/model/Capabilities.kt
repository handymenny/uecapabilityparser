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
) {
    private val metadata = HashMap<String, Any>()

    fun setMetadata(key: String, value: Any) {
        metadata[key] = value
    }

    fun getMetadata(key: String): Any? {
        return metadata.getOrDefault(key, null)
    }
}
