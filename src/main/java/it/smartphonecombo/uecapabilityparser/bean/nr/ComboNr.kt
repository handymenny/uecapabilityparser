package it.smartphonecombo.uecapabilityparser.bean.nr

import it.smartphonecombo.uecapabilityparser.bean.ICombo
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities

/** The Class Combo. */
data class ComboNr(
    override var masterComponents: Array<IComponent>,
    override var secondaryComponents: Array<IComponent>,
    val featureSet: Int = 0
) : ICombo {
    constructor(
        master: Array<IComponent>,
        secondary: Array<IComponent>
    ) : this(master, secondary, 0)
    constructor(master: Array<IComponent>, featureSet: Int) : this(master, emptyArray(), featureSet)
    constructor(master: Array<IComponent>) : this(master, emptyArray())

    /**
     * To string.
     *
     * @return the string
     * @see Object.toString
     */
    override fun toString(): String {
        val str = StringBuilder()
        for (x in componentsLte) {
            str.append(x)
            str.append("-")
        }
        if (str.length > 1) {
            str.deleteCharAt(str.length - 1)
            str.append("_")
        }
        for (x in componentsNr) {
            str.append(x.toString())
            str.append("-")
        }
        str.append(featureSet)
        return str.toString()
    }

    override fun toCsv(separator: String, standalone: Boolean): String {
        val lteDlCC =
            if (standalone) {
                0
            } else {
                ImportCapabilities.lteDlCC
            }
        val lteUlCC =
            if (standalone) {
                0
            } else {
                ImportCapabilities.lteUlCC
            }
        val band = IntArray(lteDlCC)
        val bandwidth = CharArray(lteDlCC)
        val mimo = IntArray(lteDlCC)
        val upload = CharArray(lteDlCC)
        val nrband = IntArray(ImportCapabilities.nrDlCC)
        val nrbandwidth = CharArray(ImportCapabilities.nrDlCC)
        val nrmimo = IntArray(ImportCapabilities.nrDlCC)
        val nrupload = CharArray(ImportCapabilities.nrDlCC)
        val nrmimoUL = IntArray(ImportCapabilities.nrDlCC)
        val nrmaxbandwidth = IntArray(ImportCapabilities.nrDlCC)
        val nrscs = IntArray(ImportCapabilities.nrDlCC)
        val lteModUL = arrayOfNulls<String>(lteDlCC)
        val nrModUL = arrayOfNulls<String>(ImportCapabilities.nrDlCC)
        var lteUL = ""
        var nrUL = ""
        var nrmimoULstring = ""
        val bands = componentsLte
        if (lteDlCC > 0)
            ComponentLte.lteComponentsToArrays(band, bandwidth, mimo, upload, lteModUL, bands)
        val nrbands = componentsNr
        for (i in nrbands.indices) {
            val nr = nrbands[i] as ComponentNr
            nrband[i] = nr.band
            nrmimo[i] = nr.mimoDL
            nrbandwidth[i] = nr.classDL
            nrmaxbandwidth[i] = nr.maxBandwidth
            nrupload[i] = nr.classUL
            nrmimoUL[i] = nr.mimoUL
            nrscs[i] = nr.scs
            nrModUL[i] = nr.modUL
        }
        val str = StringBuilder(this.toString() + separator)
        for (i in 0 until lteDlCC) {
            val b = band[i]
            if (b != 0) {
                str.append(b)
            }
            val bw = bandwidth[i]
            if (bw != '0' && bw != '\u0000') {
                str.append(bw)
            }
            str.append(separator)
            val ul = upload[i]
            if (ul != '0' && ul != '\u0000') {
                if (lteUL.count { it == ';' } / 2 < lteUlCC) {
                    lteUL += "" + b + ul + separator + lteModUL[i] + separator
                }
            }
        }

        while (lteUL.count { it == ';' } / 2 < lteUlCC) {
            lteUL += ";;"
        }
        if (lteUlCC > 0) {
            str.append(lteUL)
        }
        for (i in 0 until ImportCapabilities.nrDlCC) {
            val b = nrband[i]
            val bw = nrbandwidth[i]
            if (b != 0 && bw != '0') {
                str.append(b)
            }
            if (bw != '0' && bw != '\u0000') {
                str.append(bw)
            }
            str.append(separator)
            val maxbw = nrmaxbandwidth[i]
            if (maxbw != 0) {
                str.append(maxbw)
            }
            str.append(separator)
            val scs = nrscs[i]
            if (scs != 0) {
                str.append(scs)
            }
            str.append(separator)
            val ul = nrupload[i]
            if (ul != '0' && ul != '\u0000') {
                if (nrUL.count { it == ';' } / 2 < ImportCapabilities.nrUlCC) {
                    nrUL += "" + b + ul + separator + nrModUL[i] + separator
                    nrmimoULstring += "" + nrmimoUL[i] + separator
                }
            }
        }

        while (nrUL.count { it == ';' } / 2 < ImportCapabilities.nrUlCC) {
            nrUL += ";;"
        }
        str.append(nrUL)

        for (value in mimo) {
            if (value != 0) {
                str.append(value)
            }
            str.append(separator)
        }
        for (c in nrmimo) {
            if (c != 0) {
                str.append(c)
            }
            str.append(separator)
        }

        while (nrmimoULstring.count { it == ';' } < ImportCapabilities.nrUlCC) {
            nrmimoULstring += ";"
        }
        str.append(nrmimoULstring)
        return str.toString()
    }

    private val isEnDc: Boolean
        get() =
            (masterComponents.isNotEmpty() && (masterComponents[0] is ComponentLte)) &&
                (secondaryComponents.isNotEmpty() && (secondaryComponents[0] is ComponentNr))

    val componentsNr: Array<IComponent>
        get() = if (isEnDc) secondaryComponents else masterComponents

    val componentsLte: Array<IComponent>
        get() = if (isEnDc) masterComponents else secondaryComponents
}
