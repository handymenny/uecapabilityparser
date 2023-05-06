package it.smartphonecombo.uecapabilityparser.model.component

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.modulation.EmptyModulation
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComponentLte(
    @SerialName("band") override var band: Band = 0,
    @SerialName("bwClassDl") override var classDL: BwClass = BwClass.NONE,
    @SerialName("bwClassUl") override var classUL: BwClass = BwClass.NONE,
    @SerialName("mimoDl") override var mimoDL: Mimo = EmptyMimo,
    @SerialName("mimoUl") override var mimoUL: Mimo = EmptyMimo,
    @SerialName("modulationDl") override var modDL: Modulation = EmptyModulation,
    @SerialName("modulationUl") override var modUL: Modulation = EmptyModulation
) : IComponent {

    override fun compareTo(other: IComponent): Int {
        return compareValuesBy(
            this,
            other,
            { it.band },
            { it.classDL },
            { it.classUL },
            { it.mimoDL },
            { it.mimoUL }
        )
    }

    override fun clone() = copy()
}
