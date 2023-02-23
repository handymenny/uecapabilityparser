package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.util.regex.Pattern

/** The Class ImportCarrierPolicy. */
class Import0xB0CD : ImportCapabilities {
    /**
     * Convert to java class.
     *
     * @param caBandCombosString the ca band combos string
     * @return the combo list
     */
    override fun parse(caBandCombosString: String): Capabilities {
        // V41 has two columns reversed

        val isV41 = caBandCombosString.contains("UL BW Class         |DL Max Antennas Index")
        var regex =
            "^\\|\\s*\\d{1,3}\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?.*(\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?.*)?(\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?.*)?(\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?.*)?(\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?.*)?(\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?.*)?"
        if (isV41) {
            regex =
                ("^\\|\\s*\\d{1,3}\\|\\s*\\d\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?.*" +
                    "(\\s*\\|\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?.*)?" +
                    "(\\s*\\|\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?.*)?" +
                    "(\\s*\\|\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?.*)?" +
                    "(\\s*\\|\\s*\\|\\s*\\|\\s*\\d\\|\\s*(\\d{1,3})\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CA_BW_CLASS_)?([A-Ia-i1-5])?(?:NONE)?0?\\|\\s*(?:CLASS_[A-z]{1,3}_)?(\\d)(?:_\\d){0,3}(?:_ANT)?.*)?")
        }
        val pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val listCombo = ArrayList<ComboLte>()
        val matcher = pattern.matcher(caBandCombosString)
        // int x = 0;
        while (matcher.find()) {
            // System.out.println("Found " + x++);
            // System.out.println(matcher.group(0));
            val bands = ArrayList<IComponent>()
            var i = 1
            while (i < 26 && matcher.group(i) != null) {
                val baseBand = matcher.group(i++).toInt()
                var bandwidthClass = matcher.group(i++)[0]
                if (bandwidthClass < '9') {
                    bandwidthClass += 16.toChar().code
                }
                var uplink = '0'
                var mimo = 0
                if (isV41) {
                    try {
                        uplink = matcher.group(i++)[0]
                        if (uplink < '9') {
                            uplink += 16.toChar().code
                        }
                    } catch (ignored: NullPointerException) {}
                }
                try {
                    mimo = matcher.group(i++).toInt()
                } catch (ignored: NumberFormatException) {}
                if (!isV41) {
                    try {
                        uplink = matcher.group(i++)[0]
                        if (uplink < '9') {
                            uplink += 16.toChar().code
                        }
                    } catch (ignored: NullPointerException) {}
                }
                bands.add(ComponentLte(baseBand, bandwidthClass, uplink, mimo, "64qam", "16qam"))
                i++
            }
            bands.sortWith(IComponent.defaultComparator.reversed())
            val bandArray = bands.toTypedArray()
            val combo = ComboLte(bandArray)
            listCombo.add(combo)
        }
        // System.out.println(listCombo);
        return Capabilities(listCombo)
    }
}
