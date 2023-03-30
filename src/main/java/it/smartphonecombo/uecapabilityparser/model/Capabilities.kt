package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte

/** The Class ComboList. */
class Capabilities(
/** The combo list. */
) {
    var lteCombos: List<ComboLte>? = null
    var lteBands: List<ComponentLte>? = null
    var nrNSAbands: List<BandNrDetails>? = null
    var nrSAbands: List<BandNrDetails>? = null
    var nrBands: List<BandNrDetails>? = null
    var lteCategoryDL = 0
    var lteCategoryUL = 0
    private val metadata = HashMap<String, Any?>()

    /** The flags. */
    var flags = 0
    var enDcCombos: List<ComboEnDc>? = null
    var nrCombos: List<ComboNr>? = null
    var nrDcCombos: List<ComboNrDc>? = null

    constructor(
        comboList: List<ComboLte>?,
        enDCcomboList: List<ComboEnDc>?,
        flags: Int
    ) : this(comboList, flags) {
        enDcCombos = enDCcomboList
    }

    /**
     * Instantiates a new combo list.
     *
     * @param comboList the combo list
     * @param flags the flags
     */
    constructor(comboList: List<ComboLte>?, flags: Int) : this(comboList) {
        this.flags = flags
    }

    constructor(
        comboList: List<ComboLte>?,
        enDCcomboList: List<ComboEnDc>?,
        saComboList: List<ComboNr>?,
        nrNSAbands: List<BandNrDetails>?,
        nrSAbands: List<BandNrDetails>?,
        lteCategoryDL: Int,
        lteCategoryUL: Int
    ) : this(comboList, nrNSAbands, nrSAbands, lteCategoryDL, lteCategoryUL) {
        enDcCombos = enDCcomboList
        nrCombos = saComboList
    }

    constructor(
        comboList: List<ComboLte>?,
        nrNSAbands: List<BandNrDetails>?,
        nrSAbands: List<BandNrDetails>?,
        lteCategoryDL: Int,
        lteCategoryUL: Int
    ) : this(comboList) {
        this.nrNSAbands = nrNSAbands
        this.nrSAbands = nrSAbands
        this.lteCategoryDL = lteCategoryDL
        this.lteCategoryUL = lteCategoryUL
    }

    constructor(comboList: List<ComboLte>?) : this() {
        this.lteCombos = comboList
    }

    fun setMetadata(key: String, value: Any) {
        metadata[key] = value
    }

    fun getMetadata(key: String): Any? {
        return metadata.getOrDefault(key, null)
    }
}
