package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Output

data class ComboLte(
    override val masterComponents: List<ComponentLte>,
    override val bcs: BCS = EmptyBCS
) : ICombo {
    override val secondaryComponents
        get() = emptyList<IComponent>()

    override val featureSet: Int
        get() = 0

    override fun toCompactStr(): String {
        val lte =
            masterComponents.joinToString(
                separator = "-",
                transform = IComponent::toCompactStr,
            )
        return if (bcs is EmptyBCS) lte else "$lte-${bcs.toCompactStr()}"
    }

    override fun toCsv(
        separator: String,
        lteDlCC: Int,
        lteUlCC: Int,
        nrDlCC: Int,
        nrUlCC: Int,
        nrDcDlCC: Int,
        nrDcUlCC: Int
    ): String {
        val compact = this.toCompactStr() + separator
        val strBcs = bcs.toString()

        val strBand = StringBuilder()
        val strBw = StringBuilder()
        val strMimo = StringBuilder()
        val strUl = StringBuilder()
        val strULmimo = StringBuilder()
        val strDLmod = StringBuilder()
        val strULmod = StringBuilder()

        for (component in masterComponents) {
            strBand.append(component.band)
            strBw.append(component.classDL)
            strDLmod.append(component.modDL)

            val mimo = component.mimoDL
            if (mimo != EmptyMimo) strMimo.append(mimo)

            val ulClass = component.classUL
            if (ulClass != BwClass.NONE) {
                strUl.append(ulClass)
                strULmod.append(component.modUL)
                val ulMimo = component.mimoUL
                if (ulMimo != EmptyMimo) strULmimo.append(ulMimo)
            }

            Output.appendSeparator(
                separator,
                strBand,
                strBw,
                strMimo,
                strUl,
                strULmimo,
                strDLmod,
                strULmod
            )
        }

        repeat(lteDlCC - masterComponents.size) {
            Output.appendSeparator(
                separator,
                strBand,
                strBw,
                strMimo,
                strUl,
                strULmimo,
                strDLmod,
                strULmod
            )
        }

        return "$compact$strBand$strBw$strMimo$strUl$strULmimo$strDLmod$strULmod$strBcs"
    }
}
