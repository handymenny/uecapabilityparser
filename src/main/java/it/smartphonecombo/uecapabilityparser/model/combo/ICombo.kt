package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.component.IComponent

interface ICombo {
    val masterComponents: Array<out IComponent>
    val secondaryComponents: Array<out IComponent>
    val featureSet: Int
    val bcs: IntArray

    fun toCompactStr(): String

    fun toCsv(
        separator: String,
        lteDlCC: Int,
        lteUlCC: Int,
        nrDlCC: Int,
        nrUlCC: Int,
        nrDcDlCC: Int,
        nrDcUlCC: Int
    ): String
}
