package it.smartphonecombo.uecapabilityparser.model.filter

import it.smartphonecombo.uecapabilityparser.model.Rat
import it.smartphonecombo.uecapabilityparser.model.combo.ComboLte
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("UeCapabilityFilterLte")
data class UeCapabilityFilterLte(
    @Required @SerialName("rat") override var rat: Rat = Rat.EUTRA,
    @SerialName("lteBands") override var lteBands: List<BandFilterLte> = emptyList(),
    @SerialName("reducedFormat") var reducedFormat: Boolean = false,
    @SerialName("reducedIntNonContComb") var reducedIntNonContComb: Boolean = false,
    @SerialName("skipFallbackCombRequested") var skipFallbackCombRequested: Boolean = false,
    @SerialName("diffFallbackCombList") var diffFallbackCombList: List<ComboLte> = emptyList(),
    @SerialName("maxCCsDl") var maxCCsDl: Int = 0,
    @SerialName("maxCCsUl") var maxCCsUl: Int = 0,
    @SerialName("includeNrDc") override var includeNrDc: Boolean = false,
    @SerialName("includeNeDc") override var includeNeDc: Boolean = false,
    @SerialName("omitEnDc") override var omitEnDc: Boolean = false,
    @SerialName("uplinkTxSwitchRequest") override var uplinkTxSwitchRequest: Boolean = false,
) : IUeCapabilityFilter
