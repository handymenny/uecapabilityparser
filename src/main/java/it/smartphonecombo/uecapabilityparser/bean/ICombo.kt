package it.smartphonecombo.uecapabilityparser.bean

interface ICombo {
    var masterComponents: Array<IComponent>
    var secondaryComponents: Array<IComponent>

    fun toCsv(separator: String, standalone: Boolean = true): String
}