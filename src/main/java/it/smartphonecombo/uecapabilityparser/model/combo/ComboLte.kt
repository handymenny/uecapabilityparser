package it.smartphonecombo.uecapabilityparser.model.combo

import it.smartphonecombo.uecapabilityparser.model.BCS
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyBCS
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.util.Output
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComboLte(
    @SerialName("components") override val masterComponents: List<ComponentLte>,
    override val bcs: BCS = EmptyBCS
) : ICombo {
    override val secondaryComponents
        get() = emptyList<IComponent>()

    override val featureSet: Int
        get() = 0

    /** Creates a [ComboLte] merging [dlComponents] and [ulComponents] and sorting them */
    constructor(
        dlComponents: List<ComponentLte>,
        ulComponents: List<ComponentLte>
    ) : this(mergeAndSort(dlComponents, ulComponents))

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

    companion object {
        /** Merge DL Components and UL Components */
        private fun mergeAndSort(
            dlComponents: List<ComponentLte>,
            ulComponents: List<ComponentLte>
        ): List<ComponentLte> {
            val components = dlComponents.map(ComponentLte::clone).toMutableList()
            for (ulComponent in ulComponents) {
                val matchingComponent =
                    components
                        .filter { it.band == ulComponent.band && it.classUL == BwClass.NONE }
                        .maxBy(ComponentLte::classDL)

                matchingComponent.classUL = ulComponent.classUL
                matchingComponent.mimoUL = ulComponent.mimoUL
            }

            components.sortDescending()
            return components
        }
    }
}
