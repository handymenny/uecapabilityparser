package it.smartphonecombo.uecapabilityparser.model.band

import it.smartphonecombo.uecapabilityparser.extension.Band
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface IBandBoxed : Comparable<IBandBoxed> {
    var band: Band

    override fun compareTo(other: IBandBoxed): Int {
        val bandCmp = band.compareTo(other.band)

        if (bandCmp == 0) {
            // Return 0 only if they're equal
            return if (this == other) 0 else -1
        }

        return bandCmp
    }
}

@Serializable
/**
 * This is a class introduced to avoid breaking (serialization) compatibility when changing
 * [nrNSAbands][it.smartphonecombo.uecapabilityparser.model.Capabilities.nrNSAbands],
 * [nrSAbands][it.smartphonecombo.uecapabilityparser.model.Capabilities.nrSAbands] and
 * [lteBands][it.smartphonecombo.uecapabilityparser.model.filter.UeCapabilityFilterLte.lteBands]
 * type.
 *
 * It only contains the attribute [band].
 */
data class BandBoxed(@SerialName("band") override var band: Band) : IBandBoxed
