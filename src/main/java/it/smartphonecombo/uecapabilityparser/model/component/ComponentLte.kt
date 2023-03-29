package it.smartphonecombo.uecapabilityparser.model.component

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Modulation

data class ComponentLte(
    override var band: Band = 0,
    override var classDL: BwClass = BwClass.NONE,
    override var classUL: BwClass = BwClass.NONE,
    override var mimoDL: Mimo = 0,
    override var mimoUL: Mimo = 1,
    override var modDL: Modulation = Modulation.NONE,
    override var modUL: Modulation = Modulation.NONE
) : IComponent {

    override fun compareTo(other: IComponent): Int {
        return compareValuesBy(
            this,
            other,
            { it.band },
            { it.classDL },
            { it.classUL },
            { it.mimoDL },
            { it.mimoUL }
        )
    }

    companion object {
        fun lteComponentsToArrays(
            band: IntArray,
            bandwidth: Array<BwClass>,
            mimo: IntArray,
            upload: Array<BwClass>,
            modUL: Array<Modulation>,
            inputArray: Array<ComponentLte>
        ) {
            for (i in inputArray.indices) {
                band[i] = inputArray[i].band
                mimo[i] = inputArray[i].mimoDL
                bandwidth[i] = inputArray[i].classDL
                upload[i] = inputArray[i].classUL
                modUL[i] = inputArray[i].modUL
            }
        }
    }

    override fun clone() = copy()
}
