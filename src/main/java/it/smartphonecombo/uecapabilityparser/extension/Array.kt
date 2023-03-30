package it.smartphonecombo.uecapabilityparser.extension

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.util.Utility

internal fun Array<ComponentNr>.populateCsvStringBuilders(
    nrBandBwScs: StringBuilder,
    nrMimoDl: StringBuilder,
    nrUlBwMod: StringBuilder,
    nrMimoUl: StringBuilder,
    dlCCs: Int,
    ulCCs: Int,
    separator: String,
) {
    var ulCount = 0
    for (component in this) {
        nrBandBwScs.append(component.band).append(component.classDL).append(separator)

        if (component.maxBandwidth != 0) {
            nrBandBwScs.append(component.maxBandwidth)
        }
        nrBandBwScs.append(separator)

        if (component.scs != 0) {
            nrBandBwScs.append(component.scs)
        }
        nrBandBwScs.append(separator)

        if (component.mimoDL != 0) {
            nrMimoDl.append(component.mimoDL)
        }
        nrMimoDl.append(separator)

        if (component.classUL != BwClass.NONE) {
            ulCount++
            nrUlBwMod
                .append(component.band)
                .append(component.classUL)
                .append(separator)
                .append(component.modUL)
                .append(separator)

            if (component.mimoUL != 0) {
                nrMimoUl.append(component.mimoUL)
            }
            nrMimoUl.append(separator)
        }
    }

    repeat(dlCCs - this.size) {
        Utility.appendSeparator(separator, nrBandBwScs, nrBandBwScs, nrBandBwScs, nrMimoDl)
    }

    repeat(ulCCs - ulCount) { Utility.appendSeparator(separator, nrUlBwMod, nrUlBwMod, nrMimoUl) }
}
