@file:Suppress("NOTHING_TO_INLINE")

package it.smartphonecombo.uecapabilityparser.extension

import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.bandwidth.EmptyBandwidth
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.util.IO

internal inline operator fun <T> List<T>.component6(): T = get(5)

internal inline operator fun <T> List<T>.component7(): T = get(6)

internal inline fun <T> mutableListWithCapacity(capacity: Int): MutableList<T> = ArrayList(capacity)

/** Return this list as List<[T]>. It's an unchecked cast, no check is done. */
@Suppress("UNCHECKED_CAST")
internal inline fun <reified T : Any> List<*>.typedList() = this as List<T>

internal fun List<ComponentNr>.populateCsvStringBuilders(
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

        if (component.maxBandwidthDl != EmptyBandwidth) {
            nrBandBwScs.append(component.maxBandwidthDl)
        }
        nrBandBwScs.append(separator)

        if (component.scs != 0) {
            nrBandBwScs.append(component.scs)
        }
        nrBandBwScs.append(separator)

        if (component.mimoDL != EmptyMimo) {
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

            if (component.mimoUL != EmptyMimo) {
                nrMimoUl.append(component.mimoUL)
            }
            nrMimoUl.append(separator)
        }
    }

    repeat(dlCCs - this.size) {
        IO.appendSeparator(separator, nrBandBwScs, nrBandBwScs, nrBandBwScs, nrMimoDl)
    }

    repeat(ulCCs - ulCount) { IO.appendSeparator(separator, nrUlBwMod, nrUlBwMod, nrMimoUl) }
}
