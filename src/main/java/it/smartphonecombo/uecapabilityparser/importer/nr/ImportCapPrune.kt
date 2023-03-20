package it.smartphonecombo.uecapabilityparser.importer.nr

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.InputStream
import java.io.InputStreamReader

class ImportCapPrune : ImportCapabilities {

    private val regex =
        Regex(
            "([bn])(\\d{1,3})([A-Q]?)\\[?([\\d,]{0,8})]?([A-Q]?)\\[?([\\d,]{0,8})]?",
            RegexOption.IGNORE_CASE
        )

    override fun parse(input: InputStream): Capabilities {
        val caBandCombosString = input.reader().use(InputStreamReader::readText)
        val listCombo =
            caBandCombosString
                .split(';')
                .filter(String::isNotBlank)
                .map { components: String ->
                    val lteBands = ArrayList<IComponent>()
                    val nrBands = ArrayList<IComponent>()
                    components.split('-').forEach { y: String ->
                        val matchResult = regex.find(y)
                        if (matchResult != null) {
                            val groups = matchResult.groupValues
                            val baseBand = groups[2].toInt()
                            var result = groups[3]

                            val bandwidthClass = if (result.isEmpty()) '0' else result[0]
                            result = groups[5]
                            val uplink = if (result.isEmpty()) '0' else result[0]

                            val mimo = mimoParsing(groups[4])
                            if (groups[1] == "b") {
                                lteBands.add(
                                    ComponentLte(baseBand, bandwidthClass, uplink, mimo, null, null)
                                )
                            } else {
                                var ulMimo = mimoParsing(groups[6])
                                if (uplink != '0' && ulMimo == 0) {
                                    ulMimo = 1
                                }
                                nrBands.add(
                                    ComponentNr(
                                        baseBand,
                                        bandwidthClass,
                                        uplink,
                                        mimo,
                                        ulMimo,
                                        null,
                                        null
                                    )
                                )
                            }
                        }
                    }
                    lteBands.sortWith(IComponent.defaultComparator.reversed())
                    nrBands.sortWith(IComponent.defaultComparator.reversed())
                    val bandArray = lteBands.toTypedArray()
                    val nrbandsArray = nrBands.toTypedArray()
                    if (bandArray.isEmpty()) ComboNr(nrbandsArray)
                    else ComboNr(bandArray, nrbandsArray)
                }
                .toList()
        val cap = Capabilities()
        cap.enDcCombos = listCombo.filter { it.componentsLte.isNotEmpty() }
        cap.nrCombos = listCombo.filter { it.componentsLte.isEmpty() }
        return cap
    }

    private fun mimoParsing(mimo: String): Int {
        if (mimo.isEmpty()) {
            return 0
        }
        return mimo.replace(",", "").toInt()
    }
}
