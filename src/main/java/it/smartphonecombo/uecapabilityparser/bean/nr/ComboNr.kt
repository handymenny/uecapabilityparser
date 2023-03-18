package it.smartphonecombo.uecapabilityparser.bean.nr

import it.smartphonecombo.uecapabilityparser.bean.ICombo
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte

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
        if (isNrDc && str.length > 1) {
            str.deleteCharAt(str.length - 1)
            str.append("_")
        }
        for (x in componentsNrDc) {
            str.append(x.toString())
            str.append("-")
        }
        str.append(featureSet)
        return str.toString()
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
        val band = IntArray(lteDlCC)
        val bandwidth = CharArray(lteDlCC)
        val mimo = IntArray(lteDlCC)
        val upload = CharArray(lteDlCC)
        val nrband = IntArray(nrDlCC)
        val nrbandwidth = CharArray(nrDlCC)
        val nrmimo = IntArray(nrDlCC)
        val nrupload = CharArray(nrDlCC)
        val nrmimoUL = IntArray(nrDlCC)
        val nrmaxbandwidth = IntArray(nrDlCC)
        val nrscs = IntArray(nrDlCC)
        val lteModUL = arrayOfNulls<String>(lteDlCC)
        val nrModUL = arrayOfNulls<String>(nrDlCC)
        val nrbandDc = IntArray(nrDcDlCC)
        val nrbandwidthDc = CharArray(nrDcDlCC)
        val nrmimoDc = IntArray(nrDcDlCC)
        val nruploadDc = CharArray(nrDcDlCC)
        val nrmimoULDc = IntArray(nrDcDlCC)
        val nrmaxbandwidthDc = IntArray(nrDcDlCC)
        val nrscsDc = IntArray(nrDcDlCC)
        val nrModULDc = arrayOfNulls<String>(nrDcDlCC)
        var lteUL = ""
        var nrUL = ""
        var nrmimoULstring = ""
        var nrULDc = ""
        var nrmimoULstringDc = ""
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
        val nrbandsDc = componentsNrDc
        for (i in nrbandsDc.indices) {
            val nr = nrbandsDc[i] as ComponentNr
            nrbandDc[i] = nr.band
            nrmimoDc[i] = nr.mimoDL
            nrbandwidthDc[i] = nr.classDL
            nrmaxbandwidthDc[i] = nr.maxBandwidth
            nruploadDc[i] = nr.classUL
            nrmimoULDc[i] = nr.mimoUL
            nrscsDc[i] = nr.scs
            nrModULDc[i] = nr.modUL
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
        for (i in 0 until nrDlCC) {
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
                if (nrUL.count { it == ';' } / 2 < nrUlCC) {
                    nrUL += "" + b + ul + separator + nrModUL[i] + separator
                    nrmimoULstring += "" + nrmimoUL[i] + separator
                }
            }
        }
        for (i in 0 until nrDcDlCC) {
            val b = nrbandDc[i]
            val bw = nrbandwidthDc[i]
            if (b != 0 && bw != '0') {
                str.append(b)
            }
            if (bw != '0' && bw != '\u0000') {
                str.append(bw)
            }
            str.append(separator)
            val maxbw = nrmaxbandwidthDc[i]
            if (maxbw != 0) {
                str.append(maxbw)
            }
            str.append(separator)
            val scs = nrscsDc[i]
            if (scs != 0) {
                str.append(scs)
            }
            str.append(separator)
            val ul = nruploadDc[i]
            if (ul != '0' && ul != '\u0000') {
                if (nrULDc.count { it == ';' } / 2 < nrDcUlCC) {
                    nrULDc += "" + b + ul + separator + nrModULDc[i] + separator
                    nrmimoULstringDc += "" + nrmimoULDc[i] + separator
                }
            }
        }

        while (nrUL.count { it == ';' } / 2 < nrUlCC) {
            nrUL += ";;"
        }
        str.append(nrUL)

        while (nrULDc.count { it == ';' } / 2 < nrDcUlCC) {
            nrULDc += ";;"
        }
        str.append(nrULDc)

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

        for (c in nrmimoDc) {
            if (c != 0) {
                str.append(c)
            }
            str.append(separator)
        }

        while (nrmimoULstring.count { it == ';' } < nrUlCC) {
            nrmimoULstring += ";"
        }
        str.append(nrmimoULstring)

        while (nrmimoULstringDc.count { it == ';' } < nrDcUlCC) {
            nrmimoULstringDc += ";"
        }
        str.append(nrmimoULstringDc)
        return str.toString()
    }

    private val isEnDc: Boolean
        get() =
            (masterComponents.isNotEmpty() && (masterComponents[0] is ComponentLte)) &&
                (secondaryComponents.isNotEmpty() && (secondaryComponents[0] is ComponentNr))

    private val isNrDc: Boolean
        get() =
            (masterComponents.isNotEmpty() && (masterComponents[0] is ComponentNr)) &&
                (secondaryComponents.isNotEmpty() && (secondaryComponents[0] is ComponentNr))

    val componentsNr: Array<IComponent>
        get() = if (isEnDc) secondaryComponents else masterComponents

    val componentsNrDc: Array<IComponent>
        get() = if (isNrDc) secondaryComponents else emptyArray()

    val componentsLte: Array<IComponent>
        get() = if (isEnDc) masterComponents else emptyArray()
}
