package it.smartphonecombo.uecapabilityparser.newEngine

import it.smartphonecombo.uecapabilityparser.Utility
import it.smartphonecombo.uecapabilityparser.Utility.binaryStringToBcsArray
import it.smartphonecombo.uecapabilityparser.Utility.getArray
import it.smartphonecombo.uecapabilityparser.Utility.getArrayAtPath
import it.smartphonecombo.uecapabilityparser.Utility.getInt
import it.smartphonecombo.uecapabilityparser.Utility.getObject
import it.smartphonecombo.uecapabilityparser.Utility.getString
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class ImportCapabilityInformationJson : ImportCapabilities {
    private val ratTypeEutra = "eutra"
    private val ratEutraNr = "eutra-nr"
    private val ratNr = "nr"

    override fun parse(caBandCombosString: String): Capabilities {
        val caBandCombosJson = try {
            Json.parseToJsonElement(caBandCombosString) as? JsonObject
        } catch (_: SerializationException) {
            null
        }

        val eutraCapability = caBandCombosJson?.get(ratTypeEutra) as? JsonObject
        val eutraNrCapability = caBandCombosJson?.get(ratEutraNr) as? JsonObject
        val nrCapability = caBandCombosJson?.get(ratNr) as? JsonObject

        return parse(eutraCapability, eutraNrCapability, nrCapability)
    }

    fun parse(
        eutraCapability: JsonObject? = null, eutraNrCapability: JsonObject? = null, nrCapability: JsonObject? = null
    ): Capabilities {
        val comboList = Capabilities()
        eutraCapability?.let { UEEutraCapabilityJson(it) }?.let { eutra ->
            val (lteCategoryDL, lteCategoryUL) = getLTECategory(eutra)
            comboList.lteCategoryDL = lteCategoryDL
            comboList.lteCategoryUL = lteCategoryUL

            val bandList = getLteBands(eutra).associateBy({ it.band }, { it })
            comboList.nrNSAbands =
                getNrBands(eutra, true).sortedWith(compareBy { it.band })
            comboList.nrSAbands =
                getNrBands(eutra, false).sortedWith(compareBy { it.band })

            val listCombo = getBandCombinations(eutra, bandList)
            val listComboAdd = getBandCombinationsAdd(eutra, bandList)
            val listComboReduced = getBandCombinationsReduced(eutra, bandList)
            val totalLteCombos = listCombo + listComboAdd + listComboReduced

            updateLteBandsCapabilities(bandList, totalLteCombos)

            comboList.lteCombos = totalLteCombos
            comboList.lteBands = bandList.values.sortedWith(compareBy { it.band })
        }
        return comboList
    }

    private fun getBandCombinations(
        eutraCapability: UEEutraCapabilityJson, bandList: Map<Int, ComponentLte>
    ): List<ComboLte> {
        val combinations =
            eutraCapability.eutraCapabilityV1020?.getArrayAtPath("rf-Parameters-v1020.supportedBandCombination-r10")
                ?.mapNotNull { bandCombination ->
                    if (bandCombination is JsonArray) {
                        bandCombination.map { bandParameters ->
                            val band = bandParameters.getInt("bandEUTRA-r10") ?: 0

                            val bandParametersDL = bandParameters.getArrayAtPath("bandParametersDL-r10")?.get(0)
                            val dlClass =
                                bandParametersDL?.getString("ca-BandwidthClassDL-r10")?.first()?.uppercaseChar() ?: '0'
                            val mimoLayers = bandParametersDL?.getString("supportedMIMO-CapabilityDL-r10")
                            val dlMimo = Utility.convertNumber(mimoLayers?.removeSuffix("Layers"))

                            val bandParametersUL = bandParameters.getArrayAtPath("bandParametersUL-r10")?.get(0)
                            val ulClass =
                                bandParametersUL?.getString("ca-BandwidthClassUL-r10")?.first()?.uppercaseChar() ?: '0'

                            ComponentLte(band, dlClass, ulClass, dlMimo)
                        }
                    } else {
                        null
                    }
                } ?: return emptyList()

        //Bands ext
        eutraCapability.eutraCapabilityV1090?.getArrayAtPath("rf-Parameters-v1090.supportedBandCombination-v1090")
            ?.forEachIndexed { i, bandParameters ->
                if (bandParameters is JsonArray) {
                    bandParameters.forEachIndexed { j, bandParameter ->
                        bandParameter.getInt("bandEUTRA-v1090")?.let {
                            combinations[i][j].band = it
                        }
                    }
                }
            }

        // Some devices don't report 4layers in supportedMIMO-CapabilityDL-r10
        // Use CA-MIMO-ParametersDL-v10i0 if we haven't yet found any bands with 4rx or 8rx
        if (!combinations.hasHighMimo()) {
            eutraCapability.eutraCapabilityV10i0?.getArrayAtPath("rf-Parameters-v10i0.supportedBandCombination-v10i0")
                ?.let {
                    parseCaMimoV10i0(it, combinations)
                }
        }

        // Basic Modulation - set 256qam or 64qam from bandList
        setModulationFromBandList(combinations, bandList)

        // Advanced Modulation - set 1024qam DL or 256qam UL per combo
        eutraCapability.eutraCapabilityV1430?.getArrayAtPath("rf-Parameters-v1430.supportedBandCombination-v1430")
            ?.let {
                set256qamUL(it, combinations)
            }
        eutraCapability.eutraCapabilityV1530?.getArrayAtPath("rf-Parameters-v1530.supportedBandCombination-v1530")
            ?.let {
                set1024qam(it, combinations)
            }

        val supportedBandCombinationExtR10 =
            eutraCapability.eutraCapabilityV1060?.getArrayAtPath("rf-Parameters-v1060.supportedBandCombinationExt-r10")
                ?: emptyList()
        val bcsList = supportedBandCombinationExtR10.map { combinationParameters ->
            combinationParameters.getString("supportedBandwidthCombinationSet-r10")?.let {
                binaryStringToBcsArray(it)
            } ?: intArrayOf(0)
        }

        return combinations.mergeBcs(bcsList)
    }

    private fun getBandCombinationsAdd(
        eutraCapability: UEEutraCapabilityJson, bandList: Map<Int, ComponentLte>
    ): List<ComboLte> {
        val bcsList = mutableListOf<IntArray>()
        val combinations =
            eutraCapability.eutraCapabilityV1180?.getArrayAtPath("rf-Parameters-v1180.supportedBandCombinationAdd-r11")
                ?.mapNotNull { bandCombination ->
                    val bcs =
                        bandCombination.getString("supportedBandwidthCombinationSet-r11") ?: "1" // 1 -> only bcs 0
                    bcsList.add(binaryStringToBcsArray(bcs))
                    val bandParametersList = bandCombination.getArrayAtPath("bandParameterList-r11")

                    bandParametersList?.map { bandParameters ->
                        val band = bandParameters.getInt("bandEUTRA-r11") ?: 0

                        val bandParametersDL = bandParameters.getArrayAtPath("bandParametersDL-r11")?.get(0)
                        val dlClass =
                            bandParametersDL?.getString("ca-BandwidthClassDL-r10")?.first()?.uppercaseChar() ?: '0'
                        val mimoLayers = bandParametersDL?.getString("supportedMIMO-CapabilityDL-r10")
                        val dlMimo = Utility.convertNumber(mimoLayers?.removeSuffix("Layers"))

                        val bandParametersUL = bandParameters.getArrayAtPath("bandParametersUL-r11")?.get(0)
                        val ulClass =
                            bandParametersUL?.getString("ca-BandwidthClassUL-r10")?.first()?.uppercaseChar() ?: '0'
                        ComponentLte(band, dlClass, ulClass, dlMimo)
                    } ?: emptyList()
                } ?: return emptyList()


        // Some devices don't report 4layers in supportedMIMO-CapabilityDL-r10
        // Use CA-MIMO-ParametersDL-v10i0 if we haven't yet found any bands with 4rx or 8rx
        if (!combinations.hasHighMimo()) {
            eutraCapability.eutraCapabilityV11d0?.getArrayAtPath("rf-Parameters-v11d0.supportedBandCombinationAdd-v11d0")
                ?.let {
                    parseCaMimoV10i0(it, combinations)
                }
        }

        // Basic Modulation - set 256qam or 64qam from bandList
        setModulationFromBandList(combinations, bandList)

        // Advanced Modulation - set 1024qam DL or 256qam UL per combo
        eutraCapability.eutraCapabilityV1430?.getArrayAtPath("rf-Parameters-v1430.supportedBandCombinationAdd-v1430")
            ?.let {
                set256qamUL(it, combinations)
            }
        eutraCapability.eutraCapabilityV1530?.getArrayAtPath("rf-Parameters-v1530.supportedBandCombinationAdd-v1530")
            ?.let {
                set1024qam(it, combinations)
            }

        return combinations.mergeBcs(bcsList)
    }

    private fun getBandCombinationsReduced(
        eutraCapability: UEEutraCapabilityJson, bandList: Map<Int, ComponentLte>
    ): List<ComboLte> {
        val bcsList = mutableListOf<IntArray>()
        val combinations =
            eutraCapability.eutraCapabilityV1310?.getArrayAtPath("rf-Parameters-v1310.supportedBandCombinationReduced-r13")
                ?.mapNotNull { bandCombination ->
                    val bcs =
                        bandCombination.getString("supportedBandwidthCombinationSet-r13") ?: "1" // 1 -> only bcs 0
                    bcsList.add(binaryStringToBcsArray(bcs))

                    val bandParametersList = bandCombination.getArray("bandParameterList-r13")
                    bandParametersList?.map { bandParameters ->
                        val band = bandParameters.getInt("bandEUTRA-r13") ?: 0
                        val bandParametersDL = bandParameters.getObject("bandParametersDL-r13")
                        val dlClass =
                            bandParametersDL?.getString("ca-BandwidthClassDL-r13")?.first()?.uppercaseChar() ?: '0'
                        val mimoLayers = bandParametersDL?.getString("supportedMIMO-CapabilityDL-r13")
                        var dlMimo = Utility.convertNumber(mimoLayers?.removeSuffix("Layers"))
                        if (dlMimo < 4 && bandParametersDL?.getString("fourLayerTM3-TM4-r13") != null) {
                            dlMimo = 4
                        }
                        val bandParametersUL = bandParameters.getObject("bandParametersUL-r13")
                        val ulClass =
                            bandParametersUL?.getString("ca-BandwidthClassUL-r10")?.first()?.uppercaseChar() ?: '0'
                        ComponentLte(band, dlClass, ulClass, dlMimo)
                    } ?: emptyList()
                } ?: return emptyList()

        // Basic Modulation - set 256qam or 64qam from bandList
        setModulationFromBandList(combinations, bandList)

        // Advanced Modulation - set 1024qam DL or 256qam UL per combo
        eutraCapability.eutraCapabilityV1430?.getArrayAtPath("rf-Parameters-v1430.supportedBandCombinationReduced-v1430")
            ?.let {
                set256qamUL(it, combinations)
            }
        eutraCapability.eutraCapabilityV1530?.getArrayAtPath("rf-Parameters-v1530.supportedBandCombinationReduced-v1530")
            ?.let {
                set1024qam(it, combinations)
            }

        return combinations.mergeBcs(bcsList)
    }

    private fun setModulationFromBandList(
        combinations: List<List<ComponentLte>>, bandList: Map<Int, ComponentLte>
    ) {
        combinations.flatten().forEach {
            it.modDL = bandList[it.band]?.modDL
            it.modUL = bandList[it.band]?.modUL
        }
    }

    private fun set256qamUL(supportedBandCombinationV1430: JsonArray, combinations: List<List<ComponentLte>>) {
        supportedBandCombinationV1430.forEachIndexed { i, bandParameterList ->
            bandParameterList.getArray("bandParameterList-v1430")?.forEachIndexed { j, bandParameter ->
                if (bandParameter.getString("ul-256QAM-r14") != null) {
                    combinations[i][j].modUL = "256qam"
                } else {
                    bandParameter.getArray("ul-256QAM-perCC-InfoList-r14")?.firstOrNull()
                        ?.getString("ul-256QAM-perCC-r14")?.let {
                            // TODO: Handle modulation per CC
                            combinations[i][j].modUL = "256qam"
                        }
                }
            }
        }
    }

    private fun set1024qam(supportedBandCombinationV1530: JsonArray, combinations: List<List<ComponentLte>>) {
        supportedBandCombinationV1530.forEachIndexed { i, bandParameterList ->
            bandParameterList.getArray("bandParameterList-v1530")?.forEachIndexed { j, bandParameter ->
                if (bandParameter.getString("dl-1024QAM-r15") != null) {
                    combinations[i][j].modDL = "1024qam"
                }
            }
        }
    }

    private fun parseCaMimoV10i0(
        supportedBandCombinationV10i0: List<JsonElement>, combinations: List<List<ComponentLte>>
    ) {
        supportedBandCombinationV10i0.forEachIndexed { i, bandParameterList ->
            bandParameterList.getArray("bandParameterList-v10i0")?.forEachIndexed { j, it ->
                it.getArray("bandParametersDL-v10i0")?.get(0)?.getString("fourLayerTM3-TM4-r10")?.let {
                    combinations[i][j].mimoDL = 4
                }
            }
        }
    }

    private fun updateLteBandsCapabilities(bandList: Map<Int, ComponentLte>, listCombo: List<ComboLte>) {
        val lteComponents = listCombo.flatMap { it.masterComponents.toList() }.toHashSet()
        lteComponents.forEach {
            val band = bandList[it.band]
            if (band != null) {
                if (it.mimoDL > band.mimoDL) {
                    band.mimoDL = it.mimoDL
                }
                if (it.modDL == "1024qam" && it.modDL != band.modDL) {
                    band.modDL = it.modDL
                }
                if (it.modUL == "256qam" && it.modUL != band.modUL) {
                    band.modUL = it.modUL
                }
            }
        }
    }

    private fun getLTECategory(eutraCapability: UEEutraCapabilityJson): Pair<Int, Int> {
        var dlCategory = 0
        var ulCategory = 0

        // ue-Category
        eutraCapability.rootJson.getInt("ue-Category")?.let {
            dlCategory = it
            ulCategory = it
        }

        // ue-Category-v1020
        eutraCapability.eutraCapabilityV1020?.getInt("ue-Category-v1020")?.let {
            dlCategory = it
            ulCategory = it
        }

        // ue-Category-v1170
        eutraCapability.eutraCapabilityV1170?.getInt("ue-Category-v1170")?.let {
            dlCategory = it
            ulCategory = it
        }

        // ue-Category-v11a0
        eutraCapability.eutraCapabilityV11a0?.getInt("ue-Category-v11a0")?.let {
            dlCategory = it
            ulCategory = it
        }

        // ue-Category-v1250
        eutraCapability.eutraCapabilityV1250?.getInt("ue-CategoryDL-r12")?.let { dlCategory = it }
        eutraCapability.eutraCapabilityV1250?.getInt("ue-CategoryUL-r12")?.let { ulCategory = it }

        // ue-Category-v1260
        eutraCapability.eutraCapabilityV1260?.getInt("ue-CategoryDL-v1260")?.let { dlCategory = it }

        // ue-Category-v1310
        eutraCapability.eutraCapabilityV1310?.getString("ue-CategoryDL-v1310")?.let {
            // n17, m1
            dlCategory = it.drop(1).toInt()
        }
        eutraCapability.eutraCapabilityV1310?.getString("ue-CategoryUL-v1310")?.let {
            // n14, m1
            ulCategory = it.drop(1).toInt()
        }

        // ue-Category-v1330
        eutraCapability.eutraCapabilityV1330?.getInt("ue-CategoryDL-v1330")?.let { dlCategory = it }

        // ue-Category-v1340
        eutraCapability.eutraCapabilityV1340?.getInt("ue-CategoryUL-v1340")?.let { ulCategory = it }

        // ue-Category-v1350
        eutraCapability.eutraCapabilityV1350?.getInt("ue-CategoryDL-v1350")?.let { dlCategory = it }
        eutraCapability.eutraCapabilityV1350?.getInt("ue-CategoryUL-v1350")?.let { ulCategory = it }

        // ue-Category-v1430
        eutraCapability.eutraCapabilityV1430?.getString("ue-CategoryDL-v1430")?.let {
            // m2
            dlCategory = it.drop(1).toInt()
        }
        eutraCapability.eutraCapabilityV1430?.getString("ue-CategoryUL-v1430")?.let {
            // n16, n17, n18, n19, n20, m2
            ulCategory = it.drop(1).toInt()
        }
        eutraCapability.eutraCapabilityV1430?.getString("ue-CategoryUL-v1430b")?.let {
            // n21
            ulCategory = it.drop(1).toInt()
        }

        // ue-Category-v1450
        eutraCapability.eutraCapabilityV1450?.getInt("ue-CategoryDL-v1450")?.let { dlCategory = it }

        // ue-Category-v1460
        eutraCapability.eutraCapabilityV1460?.getInt("ue-CategoryDL-v1460")?.let { dlCategory = it }

        // ue-Category-v1530
        eutraCapability.eutraCapabilityV1530?.getInt("ue-CategoryDL-v1530")?.let {
            dlCategory = it
        }
        eutraCapability.eutraCapabilityV1530?.getInt("ue-CategoryUL-v1530")?.let {
            ulCategory = it
        }

        return Pair(dlCategory, ulCategory)
    }

    private fun getLteBands(eutraCapability: UEEutraCapabilityJson): List<ComponentLte> {
        val supportedBandListEutra = eutraCapability.rootJson.getArrayAtPath("rf-Parameters.supportedBandListEUTRA")

        val lteBands = supportedBandListEutra?.mapNotNull {
            it.getInt("bandEUTRA")?.let { band ->
                ComponentLte(band, 'A', 2)
            }
        } ?: return emptyList()

        eutraCapability.eutraCapabilityV9e0?.getArrayAtPath("rf-Parameters-v9e0.supportedBandListEUTRA-v9e0")
            ?.forEachIndexed { i, it ->
                it.getInt("bandEUTRA-v9e0")?.let { band ->
                    lteBands[i].band = band
                }
            }

        eutraCapability.eutraCapabilityV1250?.getArrayAtPath("rf-Parameters-v1250.supportedBandListEUTRA-v1250")
            ?.forEachIndexed { i, it ->
                if (it.getString("dl-256QAM-r12") != null) {
                    lteBands[i].modDL = "256qam"
                }
                if (it.getString("ul-64QAM-r12") != null) {
                    lteBands[i].modUL = "64qam"
                }
            }

        return lteBands
    }

    private fun getNrBands(eutraCapability: UEEutraCapabilityJson, endc: Boolean): List<ComponentNr> {

        val supportedBandListNR = if (endc) {
            eutraCapability.eutraCapabilityV1510?.getArrayAtPath("irat-ParametersNR-r15.supportedBandListEN-DC-r15")
        } else {
            eutraCapability.eutraCapabilityV1540?.getArrayAtPath("irat-ParametersNR-v1540.supportedBandListNR-SA-r15")
        }

        return supportedBandListNR?.mapNotNull {
            it.getInt("bandNR-r15")?.let { band ->
                ComponentNr(band)
            }
        } ?: emptyList()
    }

    private fun List<List<ComponentLte>>.hasHighMimo() = any { bands -> bands.any { it.mimoDL > 2 } }

    private fun List<List<ComponentLte>>.mergeBcs(bcsList: List<IntArray>) = zip(bcsList) { bands, bcs ->
        val bandArray = bands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray<IComponent>()
        ComboLte(bandArray, bcs)
    }
}