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
data class ComponentNr(
    @SerialName("band") override var band: Band = 0,
    @SerialName("bw-class-dl") override var classDL: BwClass = BwClass.NONE,
    @SerialName("bw-class-ul") override var classUL: BwClass = BwClass.NONE,
    @SerialName("mimo-dl") override var mimoDL: Mimo = EmptyMimo,
    @SerialName("mimo-ul") override var mimoUL: Mimo = EmptyMimo,
    @SerialName("modulation-dl") override var modDL: Modulation = EmptyModulation,
    @SerialName("modulation-ul") override var modUL: Modulation = EmptyModulation
) : IComponent {
    @SerialName("max-bw") var maxBandwidth = 0
    @SerialName("bw-90mhz-supported") var channelBW90mhz = false
    @SerialName("max-scs") var scs = 0

    override fun compareTo(other: IComponent): Int {
        return if (other is ComponentNr) {
            compareValuesBy(
                this,
                other,
                { it.band },
                { it.classDL },
                { it.classUL },
                { it.mimoDL },
                { it.mimoUL },
                { it.scs },
                { it.maxBandwidth }
            )
        } else {
            // Component Nr is higher than ComponentLTE
            1
        }
    }

    val isFR2: Boolean
        get() = band > 256

    override fun clone() = copy()
    override fun toCompactStr(): String = "n${super.toCompactStr()}"
}
