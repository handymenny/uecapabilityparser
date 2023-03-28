package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.model.nr.ComboNr

/** The Class ComboList. */
class Capabilities(
/** The combo list. */
) {
    var lteCombos: List<ComboLte>? = null
    var lteBands: List<ComponentLte>? = null
    var nrNSAbands: List<ComponentNr>? = null
    var nrSAbands: List<ComponentNr>? = null
    var nrBands: List<ComponentNr>? = null
    var lteCategoryDL = 0
    var lteCategoryUL = 0
    private val metadata = HashMap<String, Any?>()

    /** The flags. */
    var flags = 0
    var enDcCombos: List<ComboNr>? = null
    var nrCombos: List<ComboNr>? = null
    var nrDcCombos: List<ComboNr>? = null

    constructor(
        comboList: List<ComboLte>?,
        enDCcomboList: List<ComboNr>?,
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
        enDCcomboList: List<ComboNr>?,
        saComboList: List<ComboNr>?,
        nrNSAbands: List<ComponentNr>?,
        nrSAbands: List<ComponentNr>?,
        lteCategoryDL: Int,
        lteCategoryUL: Int
    ) : this(comboList, nrNSAbands, nrSAbands, lteCategoryDL, lteCategoryUL) {
        enDcCombos = enDCcomboList
        nrCombos = saComboList
    }

    constructor(
        comboList: List<ComboLte>?,
        nrNSAbands: List<ComponentNr>?,
        nrSAbands: List<ComponentNr>?,
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
