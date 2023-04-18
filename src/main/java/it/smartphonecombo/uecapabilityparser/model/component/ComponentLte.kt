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
    @SerialName("bw-class-dl") override var classDL: BwClass = BwClass.NONE,
    @SerialName("bw-class-ul") override var classUL: BwClass = BwClass.NONE,
    @SerialName("mimo-dl") override var mimoDL: Mimo = EmptyMimo,
    @SerialName("mimo-ul") override var mimoUL: Mimo = EmptyMimo,
    @SerialName("modulation-dl") override var modDL: Modulation = EmptyModulation,
    @SerialName("modulation-ul") override var modUL: Modulation = EmptyModulation
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
