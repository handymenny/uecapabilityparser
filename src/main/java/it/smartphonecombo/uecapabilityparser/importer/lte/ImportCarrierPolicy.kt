package it.smartphonecombo.uecapabilityparser.importer.lte

import it.smartphonecombo.uecapabilityparser.Utility
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities

/** The Class ImportCarrierPolicy. */
class ImportCarrierPolicy : ImportCapabilities {

    private val regex = Regex("(\\d{1,3})([A-F])([24]{0,5})([A-F]?)", RegexOption.IGNORE_CASE)

    /**
     * @param caBandCombosString the ca band combos string
     * @return the combo list
     */
    override fun parse(caBandCombosString: String): Capabilities {
        val listCombo =
            caBandCombosString
                .split(';', '"')
                .filter(String::isNotBlank)
                .mapNotNull { x: String ->
                    val bands = ArrayList<IComponent>()
                    val components = x.split('-')
                    val lastIndex = components.size - 1
                    for (i in 0 until lastIndex) {
                        val y = components[i]
                        val matchResult = regex.find(y)
                        if (matchResult != null) {
                            val groups = matchResult.groupValues
                            val baseBand = groups[1].toInt()
                            val bandwidthClass = groups[2][0]
                            var result = groups[3]
                            val mimo = if (result.isEmpty()) 0 else result.toInt()
                            result = groups[4]
                            val uplink = if (result.isEmpty()) '0' else result[0]
                            bands.add(
                                ComponentLte(baseBand, bandwidthClass, uplink, mimo, null, null)
                            )
                        }
                    }

                    if (bands.isEmpty()) {
                        return@mapNotNull null
                    }

                    bands.sortWith(IComponent.defaultComparator.reversed())
                    val combo = ComboLte(bands.toTypedArray())
                    val bcsString = components[lastIndex].trim()
                    if (bcsString.isNotEmpty() && bcsString != "mAll") {
                        try {
                            if (bcsString.startsWith('m')) {
                                val bcs = bcsString.substring(1).toInt(16)
                                combo.bcs = Utility.bcsToArray(bcs, true)
                            } else {
                                val bcs = bcsString.toInt()
                                combo.setSingleBcs(bcs)
                            }
                        } catch (ignored: NumberFormatException) {
                            return@mapNotNull null
                        }
                    }
                    combo
                }
                .toList()
        return Capabilities(listCombo)
    }
}
