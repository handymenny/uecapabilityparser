package it.smartphonecombo.uecapabilityparser.importer.nr

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.extension.component6
import it.smartphonecombo.uecapabilityparser.extension.component7
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.InputStream
import java.io.InputStreamReader

object ImportCapPrune : ImportCapabilities {

    override fun parse(input: InputStream): Capabilities {
        val caBandCombosString = input.reader().use(InputStreamReader::readText)

        val listCombo =
            caBandCombosString.split(';').filter(String::isNotBlank).mapNotNull(::parseCombo)

        val cap = Capabilities()
        val (enDcCombos, nrCombos) = listCombo.partition { it.componentsLte.isNotEmpty() }
        cap.enDcCombos = enDcCombos
        cap.nrCombos = nrCombos
        return cap
    }

    private fun parseCombo(comboString: String): ComboNr? {
        val lteBands = mutableListOf<IComponent>()
        val nrBands = mutableListOf<IComponent>()
        val components = comboString.split('-')

        for (componentString in components) {
            when (val component = parseComponent(componentString)) {
                is ComponentLte -> lteBands.add(component)
                is ComponentNr -> nrBands.add(component)
            }
        }

        val lteBandsArray = lteBands.toTypedArray()
        lteBandsArray.sortWith(IComponent.defaultComparator.reversed())
        val nrBandsArray = nrBands.toTypedArray()
        nrBandsArray.sortWith(IComponent.defaultComparator.reversed())

        return if (lteBandsArray.isEmpty() && nrBandsArray.isEmpty()) {
            null
        } else if (lteBandsArray.isEmpty()) {
            ComboNr(nrBandsArray)
        } else {
            ComboNr(lteBandsArray, nrBandsArray)
        }
    }

    private val componentsRegex =
        """([bn])(\d{1,3})([A-Q]?)\[?([\d,]{0,8})]?([A-Q]?)\[?([\d,]{0,8})]?""".toRegex()
    private fun parseComponent(componentString: String): IComponent? {
        val result = componentsRegex.find(componentString) ?: return null

        val (_, type, baseBand, classDL, mimoDL, classUL, mimoUL) = result.groupValues

        if (type == "b") {
            return ComponentLte(
                baseBand.toInt(),
                bwClassParsing(classDL),
                bwClassParsing(classUL),
                mimoParsing(mimoDL),
                null,
                null
            )
        } else {
            return ComponentNr(
                baseBand.toInt(),
                bwClassParsing(classDL),
                bwClassParsing(classUL),
                mimoParsing(mimoDL),
                mimoParsing(mimoUL),
                null,
                null
            )
        }
    }

    private fun mimoParsing(mimo: String): Int {
        return if (mimo.isEmpty()) {
            0
        } else {
            mimo.split(",").first().toInt()
        }
    }

    private fun bwClassParsing(bwClass: String): Char {
        return if (bwClass.isEmpty()) {
            '0'
        } else {
            bwClass[0]
        }
    }
}
