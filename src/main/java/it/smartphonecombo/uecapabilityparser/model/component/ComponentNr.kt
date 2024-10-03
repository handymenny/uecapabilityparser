package it.smartphonecombo.uecapabilityparser.model.component

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.bandwidth.Bandwidth
import it.smartphonecombo.uecapabilityparser.model.bandwidth.EmptyBandwidth
import it.smartphonecombo.uecapabilityparser.model.modulation.EmptyModulation
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComponentNr(
    @SerialName("band") override var band: Band,
    @SerialName("bwClassDl") override var classDL: BwClass = BwClass.NONE,
    @SerialName("bwClassUl") override var classUL: BwClass = BwClass.NONE,
    @SerialName("mimoDl") override var mimoDL: Mimo = EmptyMimo,
    @SerialName("mimoUl") override var mimoUL: Mimo = EmptyMimo,
    @SerialName("modulationDl") override var modDL: Modulation = EmptyModulation,
    @SerialName("modulationUl") override var modUL: Modulation = EmptyModulation,
    @SerialName("bw90mhzSupported") var channelBW90mhz: Boolean = false,
    @SerialName("maxScs") var scs: Int = 0,
    @SerialName("maxBwDl") var maxBandwidthDl: Bandwidth = EmptyBandwidth,
    @SerialName("maxBwUl") var maxBandwidthUl: Bandwidth = EmptyBandwidth,
) : IComponent {

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
                { it.maxBandwidthDl },
                { it.maxBandwidthUl },
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
