package it.smartphonecombo.uecapabilityparser.query

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import it.smartphonecombo.uecapabilityparser.model.band.BandNrDetails
import it.smartphonecombo.uecapabilityparser.model.band.IBandDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface IBandDetailsValue {
    val band: Band
    val minMimoDl: Int
    val minPowerClass: PowerClass

    fun matches(bandDetails: IBandDetails): Boolean {
        return bandDetails.band == band &&
            bandDetails.mimoDL.max() >= minMimoDl &&
            powerClassMatch(bandDetails)
    }

    fun matchesAny(list: List<IBandDetails>): Boolean = list.any { matches(it) }

    private fun powerClassMatch(bandDetails: IBandDetails): Boolean {
        if (minPowerClass == PowerClass.NONE) return true

        return bandDetails.powerClass <= minPowerClass
    }
}

@Serializable
@SerialName("lte")
data class BandLteDetailsValue(
    override val band: Band,
    override val minMimoDl: Int = 0,
    override val minPowerClass: PowerClass = PowerClass.NONE,
) : IBandDetailsValue

@Serializable
@SerialName("nr")
data class BandNrDetailsValue(
    override val band: Band,
    override val minMimoDl: Int = 0,
    val minMimoUl: Int = 0,
    override val minPowerClass: PowerClass = PowerClass.NONE,
    val supportedBw: Int = 0,
) : IBandDetailsValue {

    private fun bwMatch(bandDetails: BandNrDetails): Boolean {
        if (supportedBw == 0) return true

        val res =
            bandDetails.bandwidths.any {
                it.bwsDL.contains(supportedBw) || it.bwsUL.contains(supportedBw)
            }

        return res
    }

    override fun matches(bandDetails: IBandDetails): Boolean {
        bandDetails as BandNrDetails
        return super.matches(bandDetails) && bwMatch(bandDetails)
    }
}
