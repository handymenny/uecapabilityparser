package it.smartphonecombo.uecapabilityparser.model.band

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import it.smartphonecombo.uecapabilityparser.model.modulation.EmptyModulation
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BandLteDetails(
    @SerialName("band") override var band: Band,
    @SerialName("mimoDl") override var mimoDL: Mimo = EmptyMimo,
    @SerialName("mimoUl") override var mimoUL: Mimo = EmptyMimo,
    @SerialName("modulationDl") override var modDL: Modulation = EmptyModulation,
    @SerialName("modulationUl") override var modUL: Modulation = EmptyModulation,
    @SerialName("powerClass") override var powerClass: PowerClass = PowerClass.PC3,
) : IBandDetails, Comparable<BandLteDetails> {

    override fun compareTo(other: BandLteDetails): Int {
        val bandCmp = band.compareTo(other.band)

        if (bandCmp != 0) {
            return bandCmp
        }

        // Return 0 only if they're equal
        return if (this == other) 0 else -1
    }
}
