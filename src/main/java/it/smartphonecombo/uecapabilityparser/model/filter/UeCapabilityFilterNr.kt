package it.smartphonecombo.uecapabilityparser.model.filter

import it.smartphonecombo.uecapabilityparser.model.Rat
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("UeCapabilityFilterNr")
data class UeCapabilityFilterNr(
    @Required @SerialName("rat") override var rat: Rat,
    @SerialName("lteBands") override var lteBands: List<BandFilterLte> = emptyList(),
    @SerialName("nrBands") var nrBands: List<BandFilterNr> = emptyList(),
    @SerialName("eutraNrOnly") var eutraNrOnly: Boolean = false,
    @SerialName("includeNrDc") override var includeNrDc: Boolean = false,
    @SerialName("includeNeDc") override var includeNeDc: Boolean = false,
    @SerialName("omitEnDc") override var omitEnDc: Boolean = false,
    @SerialName("uplinkTxSwitchRequest") override var uplinkTxSwitchRequest: Boolean = false,
) : IUeCapabilityFilter
