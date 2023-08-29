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

