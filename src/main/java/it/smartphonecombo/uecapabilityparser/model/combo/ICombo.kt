package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.component.IComponent

sealed interface ICombo {
    val masterComponents: List<IComponent>
    val secondaryComponents: List<IComponent>
    val featureSet: Int
    val bcs: BCS

    fun toCompactStr(): String

    fun toCsv(
        separator: String,
        lteDlCC: Int,
        lteUlCC: Int,
        nrDlCC: Int,
        nrUlCC: Int,
        nrDcDlCC: Int,
        nrDcUlCC: Int,
    ): String
}
