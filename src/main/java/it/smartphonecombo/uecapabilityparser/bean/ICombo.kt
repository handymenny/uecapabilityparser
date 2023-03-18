package it.smartphonecombo.uecapabilityparser.bean

interface ICombo {
    var masterComponents: Array<IComponent>
    var secondaryComponents: Array<IComponent>

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
