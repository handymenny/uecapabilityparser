package it.smartphonecombo.uecapabilityparser.importer.ltenr

import it.smartphonecombo.uecapabilityparser.Utility
import it.smartphonecombo.uecapabilityparser.Utility.binaryStringToBcsArray
import it.smartphonecombo.uecapabilityparser.Utility.getArray
import it.smartphonecombo.uecapabilityparser.Utility.getArrayAtPath
import it.smartphonecombo.uecapabilityparser.Utility.getInt
import it.smartphonecombo.uecapabilityparser.Utility.getObject
import it.smartphonecombo.uecapabilityparser.Utility.getString
import it.smartphonecombo.uecapabilityparser.Utility.toBwString
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.Feature
import it.smartphonecombo.uecapabilityparser.bean.FeatureSet
import it.smartphonecombo.uecapabilityparser.bean.FeatureSets
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.Rat
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.lte.FeaturePerCCLte
import it.smartphonecombo.uecapabilityparser.bean.nr.BwTableNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.bean.nr.FeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

class ImportCapabilityInformationJson : ImportCapabilities {
    override fun parse(caBandCombosString: String): Capabilities {
        val caBandCombosJson =
            try {
                Json.parseToJsonElement(caBandCombosString) as? JsonObject
            } catch (_: SerializationException) {
                null
            }

        val eutraCapability = caBandCombosJson?.get(Rat.eutra.toString()) as? JsonObject
        val eutraNrCapability = caBandCombosJson?.get(Rat.eutra_nr.toString()) as? JsonObject
        val nrCapability = caBandCombosJson?.get(Rat.nr.toString()) as? JsonObject

        return parse(eutraCapability, eutraNrCapability, nrCapability)
    }

    fun parse(
        eutraCapability: JsonObject? = null,
        eutraNrCapability: JsonObject? = null,
        nrCapability: JsonObject? = null
    ): Capabilities {
        val comboList = Capabilities()
        var lteFeatures: FeatureSets? = null
        var nrFeatures: FeatureSets? = null

        eutraCapability
            ?.let { UEEutraCapabilityJson(it) }
            ?.let { eutra ->
                val (lteCategoryDL, lteCategoryUL) = getLTECategory(eutra)
                comboList.lteCategoryDL = lteCategoryDL
                comboList.lteCategoryUL = lteCategoryUL

                val bandList = getLteBands(eutra).associateBy({ it.band }, { it })
                comboList.nrNSAbands = getNrBands(eutra, true).sortedWith(compareBy { it.band })
                comboList.nrSAbands = getNrBands(eutra, false).sortedWith(compareBy { it.band })

                val listCombo = getBandCombinations(eutra, bandList)
                val listComboAdd = getBandCombinationsAdd(eutra, bandList)
                val listComboReduced = getBandCombinationsReduced(eutra, bandList)
                val totalLteCombos = listCombo + listComboAdd + listComboReduced

                updateLteBandsCapabilities(bandList, totalLteCombos)

                comboList.lteCombos = totalLteCombos
                comboList.lteBands = bandList.values.sortedWith(compareBy { it.band })

                if (eutraNrCapability != null) {
                    // Don't parse lte features if no mrdc capability is available
                    lteFeatures = getLteFeatureSet(eutra)
                }
            }
        nrCapability
            ?.let { UENrCapabilityJson(it) }
            ?.let { nr ->
                val bandList = getNrBands(nr)
                if (debug) {
                    bandList.forEach { println(it.toBwString()) }
                }
                comboList.nrBands = bandList
                nrFeatures = getNRFeatureSet(nr)
                val featureSetCombination = getFeatureSetCombinations(nr)
                val saCombos = getNrBandCombinations(nr)
                comboList.nrCombos =
                    linkFeaturesAndCarrier(saCombos, featureSetCombination, null, nrFeatures)
            }
        eutraNrCapability
            ?.let { UEMrdcCapabilityJson(it) }
            ?.let { mrdc ->
                val featureSetCombination = getFeatureSetCombinations(mrdc)
                val nsaCombos = getNrBandCombinations(mrdc)
                comboList.enDcCombos =
                    linkFeaturesAndCarrier(
                        nsaCombos,
                        featureSetCombination,
                        lteFeatures,
                        nrFeatures
                    )
            }
        return comboList
    }

    private fun getBandCombinations(
        eutraCapability: UEEutraCapabilityJson,
        bandList: Map<Int, ComponentLte>
    ): List<ComboLte> {
        val combinations =
            eutraCapability.eutraCapabilityV1020
                ?.getArrayAtPath("rf-Parameters-v1020.supportedBandCombination-r10")
                ?.mapNotNull { bandCombination ->
                    if (bandCombination is JsonArray) {
                        bandCombination.map { bandParameters ->
                            val band = bandParameters.getInt("bandEUTRA-r10") ?: 0

                            val bandParametersDL =
                                bandParameters.getArrayAtPath("bandParametersDL-r10")?.get(0)
                            val dlClass =
                                bandParametersDL
                                    ?.getString("ca-BandwidthClassDL-r10")
                                    ?.first()
                                    ?.uppercaseChar()
                                    ?: '0'
                            val mimoLayers =
                                bandParametersDL?.getString("supportedMIMO-CapabilityDL-r10")
                            var dlMimo = Utility.convertNumber(mimoLayers?.removeSuffix("Layers"))

                            // Some devices don't report supportedMIMO-CapabilityDL-r10 for
                            // twoLayers
                            if (dlClass != '0' && dlMimo == 0) {
                                dlMimo = 2
                            }

                            val bandParametersUL =
                                bandParameters.getArrayAtPath("bandParametersUL-r10")?.get(0)
                            val ulClass =
                                bandParametersUL
                                    ?.getString("ca-BandwidthClassUL-r10")
                                    ?.first()
                                    ?.uppercaseChar()
                                    ?: '0'

                            ComponentLte(band, dlClass, ulClass, dlMimo)
                        }
                    } else {
                        null
                    }
                }
                ?: return emptyList()

        // Bands ext
        eutraCapability.eutraCapabilityV1090
            ?.getArrayAtPath("rf-Parameters-v1090.supportedBandCombination-v1090")
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
            eutraCapability.eutraCapabilityV10i0
                ?.getArrayAtPath("rf-Parameters-v10i0.supportedBandCombination-v10i0")
                ?.let { parseCaMimoV10i0(it, combinations) }
        }

        // Basic Modulation - set 256qam or 64qam from bandList
        setModulationFromBandList(combinations, bandList)

        // Advanced Modulation - set 1024qam DL or 256qam UL per combo
        eutraCapability.eutraCapabilityV1430
            ?.getArrayAtPath("rf-Parameters-v1430.supportedBandCombination-v1430")
            ?.let { set256qamUL(it, combinations) }
        eutraCapability.eutraCapabilityV1530
            ?.getArrayAtPath("rf-Parameters-v1530.supportedBandCombination-v1530")
            ?.let { set1024qam(it, combinations) }

        val supportedBandCombinationExtR10 =
            eutraCapability.eutraCapabilityV1060?.getArrayAtPath(
                "rf-Parameters-v1060.supportedBandCombinationExt-r10"
            )
                ?: emptyList()
        val bcsList =
            supportedBandCombinationExtR10.map { combinationParameters ->
                combinationParameters.getString("supportedBandwidthCombinationSet-r10")?.let {
                    binaryStringToBcsArray(it)
                }
                    ?: intArrayOf(0)
            }

        return combinations.mergeBcs(bcsList)
    }

    private fun getBandCombinationsAdd(
        eutraCapability: UEEutraCapabilityJson,
        bandList: Map<Int, ComponentLte>
    ): List<ComboLte> {
        val bcsList = mutableListOf<IntArray>()
        val combinations =
            eutraCapability.eutraCapabilityV1180
                ?.getArrayAtPath("rf-Parameters-v1180.supportedBandCombinationAdd-r11")
                ?.mapNotNull { bandCombination ->
                    val bcs =
                        bandCombination.getString("supportedBandwidthCombinationSet-r11")
                            ?: "1" // 1 -> only bcs 0
                    bcsList.add(binaryStringToBcsArray(bcs))
                    val bandParametersList = bandCombination.getArrayAtPath("bandParameterList-r11")

                    bandParametersList?.map { bandParameters ->
                        val band = bandParameters.getInt("bandEUTRA-r11") ?: 0

                        val bandParametersDL =
                            bandParameters.getArrayAtPath("bandParametersDL-r11")?.get(0)
                        val dlClass =
                            bandParametersDL
                                ?.getString("ca-BandwidthClassDL-r10")
                                ?.first()
                                ?.uppercaseChar()
                                ?: '0'
                        val mimoLayers =
                            bandParametersDL?.getString("supportedMIMO-CapabilityDL-r10")
                        var dlMimo = Utility.convertNumber(mimoLayers?.removeSuffix("Layers"))

                        // Some devices don't report supportedMIMO-CapabilityDL-r10 for twoLayers
                        if (dlClass != '0' && dlMimo == 0) {
                            dlMimo = 2
                        }

                        val bandParametersUL =
                            bandParameters.getArrayAtPath("bandParametersUL-r11")?.get(0)
                        val ulClass =
                            bandParametersUL
                                ?.getString("ca-BandwidthClassUL-r10")
                                ?.first()
                                ?.uppercaseChar()
                                ?: '0'
                        ComponentLte(band, dlClass, ulClass, dlMimo)
                    }
                        ?: emptyList()
                }
                ?: return emptyList()

        // Some devices don't report 4layers in supportedMIMO-CapabilityDL-r10
        // Use CA-MIMO-ParametersDL-v10i0 if we haven't yet found any bands with 4rx or 8rx
        if (!combinations.hasHighMimo()) {
            eutraCapability.eutraCapabilityV11d0
                ?.getArrayAtPath("rf-Parameters-v11d0.supportedBandCombinationAdd-v11d0")
                ?.let { parseCaMimoV10i0(it, combinations) }
        }

        // Basic Modulation - set 256qam or 64qam from bandList
        setModulationFromBandList(combinations, bandList)

        // Advanced Modulation - set 1024qam DL or 256qam UL per combo
        eutraCapability.eutraCapabilityV1430
            ?.getArrayAtPath("rf-Parameters-v1430.supportedBandCombinationAdd-v1430")
            ?.let { set256qamUL(it, combinations) }
        eutraCapability.eutraCapabilityV1530
            ?.getArrayAtPath("rf-Parameters-v1530.supportedBandCombinationAdd-v1530")
            ?.let { set1024qam(it, combinations) }

        return combinations.mergeBcs(bcsList)
    }

    private fun getBandCombinationsReduced(
        eutraCapability: UEEutraCapabilityJson,
        bandList: Map<Int, ComponentLte>
    ): List<ComboLte> {
        val bcsList = mutableListOf<IntArray>()
        val combinations =
            eutraCapability.eutraCapabilityV1310
                ?.getArrayAtPath("rf-Parameters-v1310.supportedBandCombinationReduced-r13")
                ?.mapNotNull { bandCombination ->
                    val bcs =
                        bandCombination.getString("supportedBandwidthCombinationSet-r13")
                            ?: "1" // 1 -> only bcs 0
                    bcsList.add(binaryStringToBcsArray(bcs))

                    val bandParametersList = bandCombination.getArray("bandParameterList-r13")
                    bandParametersList?.map { bandParameters ->
                        val band = bandParameters.getInt("bandEUTRA-r13") ?: 0
                        val bandParametersDL = bandParameters.getObject("bandParametersDL-r13")
                        val dlClass =
                            bandParametersDL
                                ?.getString("ca-BandwidthClassDL-r13")
                                ?.first()
                                ?.uppercaseChar()
                                ?: '0'
                        val mimoLayers =
                            bandParametersDL?.getString("supportedMIMO-CapabilityDL-r13")
                        var dlMimo = Utility.convertNumber(mimoLayers?.removeSuffix("Layers"))

                        // Some devices only reports fourLayerTM3-TM4-r13 or only reports 4rx in
                        // fourLayerTM3-TM4-r13
                        if (
                            dlMimo < 4 &&
                                bandParametersDL?.getString("fourLayerTM3-TM4-r13") != null
                        ) {
                            dlMimo = 4
                        }
                        // Some devices don't report supportedMIMO-CapabilityDL-r13 for twoLayers
                        if (dlClass != '0' && dlMimo == 0) {
                            dlMimo = 2
                        }
                        val bandParametersUL = bandParameters.getObject("bandParametersUL-r13")
                        val ulClass =
                            bandParametersUL
                                ?.getString("ca-BandwidthClassUL-r10")
                                ?.first()
                                ?.uppercaseChar()
                                ?: '0'
                        ComponentLte(band, dlClass, ulClass, dlMimo)
                    }
                        ?: emptyList()
                }
                ?: return emptyList()

        // Basic Modulation - set 256qam or 64qam from bandList
        setModulationFromBandList(combinations, bandList)

        // Advanced Modulation - set 1024qam DL or 256qam UL per combo
        eutraCapability.eutraCapabilityV1430
            ?.getArrayAtPath("rf-Parameters-v1430.supportedBandCombinationReduced-v1430")
            ?.let { set256qamUL(it, combinations) }
        eutraCapability.eutraCapabilityV1530
            ?.getArrayAtPath("rf-Parameters-v1530.supportedBandCombinationReduced-v1530")
            ?.let { set1024qam(it, combinations) }

        return combinations.mergeBcs(bcsList)
    }

    private fun setModulationFromBandList(
        combinations: List<List<ComponentLte>>,
        bandList: Map<Int, ComponentLte>
    ) {
        combinations.flatten().forEach {
            it.modDL = bandList[it.band]?.modDL
            it.modUL = bandList[it.band]?.modUL
        }
    }

    private fun set256qamUL(
        supportedBandCombinationV1430: JsonArray,
        combinations: List<List<ComponentLte>>
    ) {
        supportedBandCombinationV1430.forEachIndexed { i, bandParameterList ->
            bandParameterList.getArray("bandParameterList-v1430")?.forEachIndexed { j, bandParameter
                ->
                if (bandParameter.getString("ul-256QAM-r14") != null) {
                    combinations[i][j].modUL = "256qam"
                } else {
                    bandParameter
                        .getArray("ul-256QAM-perCC-InfoList-r14")
                        ?.firstOrNull()
                        ?.getString("ul-256QAM-perCC-r14")
                        ?.let {
                            // TODO: Handle modulation per CC
                            combinations[i][j].modUL = "256qam"
                        }
                }
            }
        }
    }

    private fun set1024qam(
        supportedBandCombinationV1530: JsonArray,
        combinations: List<List<ComponentLte>>
    ) {
        supportedBandCombinationV1530.forEachIndexed { i, bandParameterList ->
            bandParameterList.getArray("bandParameterList-v1530")?.forEachIndexed { j, bandParameter
                ->
                if (bandParameter.getString("dl-1024QAM-r15") != null) {
                    combinations[i][j].modDL = "1024qam"
                }
            }
        }
    }

    private fun parseCaMimoV10i0(
        supportedBandCombinationV10i0: List<JsonElement>,
        combinations: List<List<ComponentLte>>
    ) {
        supportedBandCombinationV10i0.forEachIndexed { i, bandParameterList ->
            bandParameterList.getArray("bandParameterList-v10i0")?.forEachIndexed { j, it ->
                it.getArray("bandParametersDL-v10i0")
                    ?.get(0)
                    ?.getString("fourLayerTM3-TM4-r10")
                    ?.let { combinations[i][j].mimoDL = 4 }
            }
        }
    }

    private fun updateLteBandsCapabilities(
        bandList: Map<Int, ComponentLte>,
        listCombo: List<ComboLte>
    ) {
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
        eutraCapability.eutraCapabilityV1530?.getInt("ue-CategoryDL-v1530")?.let { dlCategory = it }
        eutraCapability.eutraCapabilityV1530?.getInt("ue-CategoryUL-v1530")?.let { ulCategory = it }

        return Pair(dlCategory, ulCategory)
    }

    private fun getLteBands(eutraCapability: UEEutraCapabilityJson): List<ComponentLte> {
        val supportedBandListEutra =
            eutraCapability.rootJson.getArrayAtPath("rf-Parameters.supportedBandListEUTRA")

        val lteBands =
            supportedBandListEutra?.mapNotNull {
                it.getInt("bandEUTRA")?.let { band -> ComponentLte(band, 'A', 2) }
            }
                ?: return emptyList()

        eutraCapability.eutraCapabilityV9e0
            ?.getArrayAtPath("rf-Parameters-v9e0.supportedBandListEUTRA-v9e0")
            ?.forEachIndexed { i, it ->
                it.getInt("bandEUTRA-v9e0")?.let { band -> lteBands[i].band = band }
            }

        eutraCapability.eutraCapabilityV1250
            ?.getArrayAtPath("rf-Parameters-v1250.supportedBandListEUTRA-v1250")
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

    private fun getNrBands(
        eutraCapability: UEEutraCapabilityJson,
        endc: Boolean
    ): List<ComponentNr> {

        val supportedBandListNR =
            if (endc) {
                eutraCapability.eutraCapabilityV1510?.getArrayAtPath(
                    "irat-ParametersNR-r15.supportedBandListEN-DC-r15"
                )
            } else {
                eutraCapability.eutraCapabilityV1540?.getArrayAtPath(
                    "irat-ParametersNR-v1540.supportedBandListNR-SA-r15"
                )
            }

        return supportedBandListNR?.mapNotNull {
            it.getInt("bandNR-r15")?.let { band -> ComponentNr(band) }
        }
            ?: emptyList()
    }

    private fun List<List<ComponentLte>>.hasHighMimo() = any { bands ->
        bands.any { it.mimoDL > 2 }
    }

    private fun List<List<ComponentLte>>.mergeBcs(bcsList: List<IntArray>) =
        zip(bcsList) { bands, bcs ->
            val bandArray =
                bands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray<IComponent>()
            ComboLte(bandArray, bcs)
        }

    private fun linkFeaturesAndCarrier(
        combos: List<ComboNr>,
        featureSetCombinations: List<List<List<Feature>>>,
        lteFeatures: FeatureSets?,
        nrFeatures: FeatureSets?
    ): List<ComboNr> {
        val list = mutableListOf<ComboNr>()

        for (combo in combos) {
            val featureSetsPerBand = featureSetCombinations.getOrNull(combo.featureSet) ?: continue
            val indices = featureSetsPerBand.firstOrNull()?.indices ?: IntRange.EMPTY

            for (index in indices) {
                val newNrComponents = mutableListOf<ComponentNr>()
                val newLteComponents = mutableListOf<ComponentLte>()

                val lteComponents = combo.componentsLte.iterator()
                val nrComponents = combo.componentsNr.iterator()

                for (featureSetBand in featureSetsPerBand) {
                    val featureSet = featureSetBand[index]

                    // Index starts from 1
                    val downlinkIndex = featureSet.downlink - 1
                    val uplinkIndex = featureSet.uplink - 1

                    if (downlinkIndex < 0 && uplinkIndex < 0) {
                        // Fallback combination
                        if (featureSet.isNR) {
                            nrComponents.next()
                        } else {
                            lteComponents.next()
                        }
                        continue
                    }

                    if (featureSet.isNR) {
                        val oldComponentNr = nrComponents.next() as ComponentNr
                        val componentNr =
                            ComponentNr(
                                oldComponentNr.band,
                                oldComponentNr.classDL,
                                oldComponentNr.classUL
                            )

                        // TODO: Features per CC
                        if (downlinkIndex >= 0) {
                            val dlFeature =
                                nrFeatures?.downlink?.get(downlinkIndex)?.featureSetsPerCC?.get(0)
                                    as? FeaturePerCCNr
                            if (dlFeature != null) {
                                componentNr.mimoDL = dlFeature.mimo
                                componentNr.modDL = dlFeature.qam
                                componentNr.maxBandwidth = dlFeature.bw
                                componentNr.channelBW90mhz =
                                    dlFeature.bw >= 80 && dlFeature.channelBW90mhz
                                componentNr.scs = dlFeature.scs
                            }
                        } else {
                            // only UL
                            componentNr.classDL = '0'
                            componentNr.mimoDL = 0
                        }

                        if (uplinkIndex >= 0) {
                            val ulFeature =
                                nrFeatures?.uplink?.get(uplinkIndex)?.featureSetsPerCC?.get(0)
                                    as? FeaturePerCCNr
                            if (ulFeature != null) {
                                componentNr.mimoUL = ulFeature.mimo
                                componentNr.modUL = ulFeature.qam
                            }
                        } else {
                            // only DL
                            componentNr.classUL = '0'
                            componentNr.mimoUL = 0
                        }
                        newNrComponents.add(componentNr)
                    } else {
                        val oldComponentLte = lteComponents.next() as ComponentLte
                        val componentLte =
                            ComponentLte(
                                oldComponentLte.band,
                                oldComponentLte.classDL,
                                oldComponentLte.classUL
                            )

                        // TODO: Features per CC
                        if (downlinkIndex >= 0) {
                            val dlFeature =
                                lteFeatures?.downlink?.get(downlinkIndex)?.featureSetsPerCC?.get(0)
                            if (dlFeature != null) {
                                componentLte.mimoDL = dlFeature.mimo
                            }
                        } else {
                            // only UL
                            componentLte.classDL = '0'
                            componentLte.mimoDL = 0
                        }

                        if (uplinkIndex >= 0) {
                            val ulFeature =
                                lteFeatures?.uplink?.get(uplinkIndex)?.featureSetsPerCC?.get(0)
                            if (ulFeature != null) {
                                componentLte.mimoUL = ulFeature.mimo
                                componentLte.modUL = ulFeature.qam
                            }
                        } else {
                            // only DL
                            componentLte.classUL = '0'
                            componentLte.mimoUL = 0
                        }
                        newLteComponents.add(componentLte)
                    }
                }

                val nrArray =
                    newNrComponents
                        .sortedWith(IComponent.defaultComparator.reversed())
                        .toTypedArray<IComponent>()

                val comboNr =
                    if (newLteComponents.isNotEmpty()) {
                        val lteArray =
                            newLteComponents
                                .sortedWith(IComponent.defaultComparator.reversed())
                                .toTypedArray<IComponent>()
                        ComboNr(lteArray, nrArray, combo.featureSet)
                    } else {
                        ComboNr(nrArray, combo.featureSet)
                    }

                list.add(comboNr)
            }
        }
        return list
    }
    private fun getFeatureSetCombinations(
        nrCapability: UENrRrcCapabilityJson
    ): List<List<List<Feature>>> {
        val featureSetCombinations = nrCapability.rootJson.getArray("featureSetCombinations")
        val list =
            featureSetCombinations?.mapNotNull { featureSetCombination ->
                (featureSetCombination as? JsonArray)?.mapNotNull { featureSetsPerBand ->
                    (featureSetsPerBand as? JsonArray)?.mapNotNull { featureSet ->
                        val eutra = featureSet.getObject("eutra")
                        val nr = featureSet.getObject("nr")
                        if (nr != null) {
                            val dl = nr.getInt("downlinkSetNR") ?: 0
                            val ul = nr.getInt("uplinkSetNR") ?: 0

                            Feature(true, dl, ul)
                        } else if (eutra != null) {
                            val dl = eutra.getInt("downlinkSetEUTRA") ?: 0
                            val ul = eutra.getInt("uplinkSetEUTRA") ?: 0

                            Feature(false, dl, ul)
                        } else {
                            null
                        }
                    }
                }
            }
        return list ?: emptyList()
    }

    private fun getNrBandCombinations(nrCapability: UENrRrcCapabilityJson): List<ComboNr> {
        val endc = nrCapability is UEMrdcCapabilityJson
        val bandCombinationsPath =
            if (endc) {
                "rf-ParametersMRDC.supportedBandCombinationList"
            } else {
                "rf-Parameters.supportedBandCombinationList"
            }
        val bandCombinationsList = nrCapability.rootJson.getArrayAtPath(bandCombinationsPath)
        val list =
            bandCombinationsList
                ?.mapNotNull { bandCombination ->
                    val bandList = bandCombination.getArray("bandList")
                    if (bandList is JsonArray) {
                        val lteBands = mutableListOf<ComponentLte>()
                        val nrBands = mutableListOf<ComponentNr>()
                        bandList.forEach { bandParameters ->
                            val nr = bandParameters.getObject("nr")
                            val lte = if (endc) bandParameters.getObject("eutra") else null
                            if (nr != null) {
                                val band = nr.getInt("bandNR") ?: 0
                                val dlClass =
                                    nr.getString("ca-BandwidthClassDL-NR")?.first()?.uppercaseChar()
                                        ?: '0'
                                val ulClass =
                                    nr.getString("ca-BandwidthClassUL-NR")?.first()?.uppercaseChar()
                                        ?: '0'
                                nrBands.add(ComponentNr(band, dlClass, ulClass))
                            } else if (endc && lte != null) {
                                val band = lte.getInt("bandEUTRA") ?: 0
                                val dlClass =
                                    lte.getString("ca-BandwidthClassDL-EUTRA")
                                        ?.first()
                                        ?.uppercaseChar()
                                        ?: '0'
                                val ulClass =
                                    lte.getString("ca-BandwidthClassUL-EUTRA")
                                        ?.first()
                                        ?.uppercaseChar()
                                        ?: '0'
                                lteBands.add(ComponentLte(band, dlClass, ulClass))
                            }
                        }
                        val featureSetCombination =
                            bandCombination.getInt("featureSetCombination") ?: 0
                        if (endc) {
                            ComboNr(
                                lteBands.toTypedArray(),
                                nrBands.toTypedArray(),
                                featureSetCombination
                            )
                        } else {
                            ComboNr(nrBands.toTypedArray(), featureSetCombination)
                        }
                    } else {
                        null
                    }
                }
                ?.toList()

        return list ?: emptyList()
    }

    private fun getNrBands(nrCapability: UENrCapabilityJson): List<ComponentNr> {
        return nrCapability.rootJson
            .getArrayAtPath("rf-Parameters.supportedBandListNR")
            ?.mapNotNull { supportedBandNr ->
                val componentNr =
                    supportedBandNr.getInt("bandNR")?.let { ComponentNr(it) }
                        ?: return@mapNotNull null

                if (componentNr.isFR2 && supportedBandNr.getString("pdsch-256QAM-FR2") != null) {
                    componentNr.modDL = "256qam"
                }

                if (supportedBandNr.getString("pusch-256QAM") != null) {
                    componentNr.modUL = "256qam"
                }

                supportedBandNr.getString("ue-PowerClass")?.removePrefix("pc")?.toInt()?.let {
                    componentNr.powerClass = it
                }

                if (supportedBandNr.getString("rateMatchingLTE-CRS") != null) {
                    componentNr.rateMatchingLTEcrs = true
                }

                val maxUplinkDutyCycleKey =
                    if (componentNr.isFR2) {
                        "maxUplinkDutyCycle-FR2"
                    } else {
                        "maxUplinkDutyCycle-PC2-FR1"
                    }
                supportedBandNr.getString(maxUplinkDutyCycleKey)?.removePrefix("n")?.toInt()?.let {
                    componentNr.maxUplinkDutyCycle = it
                }

                parseNRChannelBWs(supportedBandNr, componentNr)

                componentNr
            }
            ?.toList()
            ?: emptyList()
    }

    private fun parseNRChannelBWs(supportedBandNr: JsonElement, componentNr: ComponentNr) {
        val channelBWsDL = supportedBandNr.getObject("channelBWs-DL")
        val channelBWsUL = supportedBandNr.getObject("channelBWs-UL")
        val channelBWsDlV1590 = supportedBandNr.getObject("channelBWs-DL-v1590")
        val channelBWsUlV1590 = supportedBandNr.getObject("channelBWs-UL-v1590")

        val bandwidthsDL =
            parseNrBw(channelBWsDL, componentNr, false)
                .merge(parseNrBw(channelBWsDlV1590, componentNr, true))
        val bandwidthsUL =
            parseNrBw(channelBWsUL, componentNr, false)
                .merge(parseNrBw(channelBWsUlV1590, componentNr, true))

        val scsRange =
            if (componentNr.isFR2) {
                60..120 step { it * 2 }
            } else {
                15..60 step { it * 2 }
            }

        /*
         * According to TS 38.306 v16.6.0, the UE can omit channelBWs or specific SCS in channelBWs
         * if it supports all BWs defined in 38.101-1 and 38.101-2 v15.7.0.
         * So we add default BWs here when a specific SCS is missing.
         *
         * We also use this cycle to sort bws array.
         */
        for (scs in scsRange) {
            val bws = BwTableNr.getDLBws(componentNr.band, scs)
            if (!bandwidthsDL.containsKey(scs)) {
                bandwidthsDL[scs] = bws.bwsDL
            } else {
                // Sort bws array
                bandwidthsDL[scs] = bandwidthsDL[scs]!!.sortedArrayDescending()
            }
            if (!bandwidthsUL.containsKey(scs)) {
                bandwidthsUL[scs] = bws.bwsUL
            } else {
                // Sort bws array
                bandwidthsUL[scs] = bandwidthsUL[scs]!!.sortedArrayDescending()
            }
        }
        componentNr.bandwidthsDL = bandwidthsDL
        componentNr.bandwidthsUL = bandwidthsUL
    }

    private fun parseNrBw(
        channelBWsDL: JsonObject?,
        componentNr: ComponentNr,
        isV1590: Boolean = false
    ): Map<Int, IntArray> {
        /*
         * According to TS 38.306 v16.6.0 there's no 100MHz field for n41, n48, n77, n78, n79, n90
         * So we assume that it's supported by default.
         * Add 100 MHz only for channelBWs (not for its extensions), to avoid duplicates
         */
        val default100MHz = !isV1590 && componentNr.band in listOf(41, 48, 77, 78, 79, 90)
        val freqRange = if (componentNr.isFR2) "fr2" else "fr1"

        val bandWidthMap = mutableMapOf<Int, IntArray>()
        channelBWsDL?.getObject(freqRange)?.forEach { (scsKey, element) ->
            val scs = scsKey.removePrefix("scs-").removeSuffix("kHz").toInt()
            (element as? JsonPrimitive)?.contentOrNull?.let { bwString ->
                var bws = Utility.bwStringToArray(bwString, componentNr.isFR2, isV1590)
                if (scs != 15 && default100MHz && bws.isNotEmpty()) {
                    bws += 100
                }
                bandWidthMap[scs] = bws
            }
        }
            ?: return emptyMap()
        return bandWidthMap
    }

    private fun getLteFeatureSet(eutraCapability: UEEutraCapabilityJson): FeatureSets {
        var downlink = emptyList<FeatureSet>()
        var uplink = emptyList<FeatureSet>()

        eutraCapability.eutraCapabilityV1510?.getObject("featureSetsEUTRA-r15")?.let { featureSets
            ->
            val downlinkPerCC =
                featureSets.getArray("featureSetsDL-PerCC-r15")?.map {
                    val qam = "256qam"
                    val mimoLayers = it.getString("supportedMIMO-CapabilityDL-MRDC-r15")
                    val mimo = maxOf(Utility.convertNumber(mimoLayers?.removeSuffix("Layers")), 2)
                    FeaturePerCCLte(mimo = mimo, qam = qam)
                }

            downlink =
                featureSets.getArray("featureSetsDL-r15")?.mapNotNull { featureSetPerCCList ->
                    val list =
                        featureSetPerCCList.getArray("featureSetPerCC-ListDL-r15")?.mapNotNull {
                            index ->
                            (index as? JsonPrimitive)?.intOrNull?.let {
                                downlinkPerCC?.getOrNull(it)
                            }
                        }
                    FeatureSet(list, FeatureSet.DOWNlINK)
                }
                    ?: downlink

            val uplinkPerCC =
                featureSets.getArray("featureSetsUL-PerCC-r15")?.map {
                    val qam =
                        if (it.getString("ul-256QAM-r15") != null) {
                            "256qam"
                        } else {
                            "64qam"
                        }
                    val mimoLayers = it.getString("supportedMIMO-CapabilityUL-r15")
                    val mimo = maxOf(Utility.convertNumber(mimoLayers?.removeSuffix("Layers")), 1)
                    FeaturePerCCLte(FeaturePerCCLte.UPLINK, mimo = mimo, qam = qam)
                }

            // featureSets.getArray("featureSetsDL-v1550") - Never seen on the wild
            uplink =
                featureSets.getArray("featureSetsUL-r15")?.mapNotNull { featureSetPerCCList ->
                    val list =
                        featureSetPerCCList.getArray("featureSetPerCC-ListUL-r15")?.mapNotNull {
                            index ->
                            (index as? JsonPrimitive)?.intOrNull?.let { uplinkPerCC?.getOrNull(it) }
                        }
                    FeatureSet(list, FeatureSet.UPLINK)
                }
                    ?: uplink
        }

        if (debug) {
            println("\nLTE FeatureSets")
            println(downlink.joinToString(separator = "\n"))
            println(uplink.joinToString(separator = "\n"))
        }
        return FeatureSets(downlink, uplink)
    }

    private fun getNRFeatureSet(nrCapability: UENrCapabilityJson): FeatureSets {
        var downlink = emptyList<FeatureSet>()
        var uplink = emptyList<FeatureSet>()

        nrCapability.rootJson.getObject("featureSets")?.let { featureSets ->
            val downlinkPerCC =
                featureSets.getArray("featureSetsDownlinkPerCC")?.map {
                    val scs =
                        it.getString("supportedSubcarrierSpacingDL")
                            ?.removePrefix("kHz")
                            ?.split("-")
                            ?.first()
                            ?.toIntOrNull()
                            ?: 0
                    val supportedBandwidthDL = it.getObject("supportedBandwidthDL")
                    val bwFr1OrFr2 =
                        supportedBandwidthDL?.getString("fr1")
                            ?: supportedBandwidthDL?.getString("fr2")
                    val bw = bwFr1OrFr2?.removePrefix("mhz")?.toIntOrNull() ?: 0
                    val channelBW90mhz = it.getString("channelBW-90mhz") == "true"
                    val mimoLayers = it.getString("maxNumberMIMO-LayersPDSCH")
                    val mimo = maxOf(Utility.convertNumber(mimoLayers?.removeSuffix("Layers")), 2)
                    val qam =
                        it.getString("supportedModulationOrderDL")?.removePrefix("qam").plus("qam")

                    FeaturePerCCNr(
                        mimo = mimo,
                        qam = qam,
                        scs = scs,
                        bw = bw,
                        channelBW90mhz = channelBW90mhz
                    )
                }

            downlink =
                featureSets.getArray("featureSetsDownlink")?.mapNotNull { featureSetPerCCList ->
                    val list =
                        featureSetPerCCList.getArray("featureSetListPerDownlinkCC")?.mapNotNull {
                            index ->
                            (index as? JsonPrimitive)?.intOrNull?.let {
                                // NR PerCC-ID is 1..1024 while LTE PerCC-ID is 0..32
                                downlinkPerCC?.getOrNull(it - 1)
                            }
                        }
                    FeatureSet(list, FeatureSet.DOWNlINK)
                }
                    ?: downlink

            val uplinkPerCC =
                featureSets.getArray("featureSetsUplinkPerCC")?.map {
                    val scs =
                        it.getString("supportedSubcarrierSpacingUL")
                            ?.removePrefix("kHz")
                            ?.split("-")
                            ?.first()
                            ?.toIntOrNull()
                            ?: 0
                    val supportedBandwidthUL = it.getObject("supportedBandwidthUL")
                    val bwFr1OrFr2 =
                        supportedBandwidthUL?.getString("fr1")
                            ?: supportedBandwidthUL?.getString("fr2")
                    val bw = bwFr1OrFr2?.removePrefix("mhz")?.toIntOrNull() ?: 0
                    val channelBW90mhz = it.getString("channelBW-90mhz") == "true"

                    val mimoCbLayers =
                        it.getObject("mimo-CB-PUSCH")
                            ?.getString("maxNumberMIMO-LayersCB-PUSCH")
                            ?.removeSuffix("Layers")
                    val mimoNonCbLayers =
                        it.getString("maxNumberMIMO-LayersNonCB-PUSCH")?.removeSuffix("Layers")

                    val mimo =
                        maxOf(
                            Utility.convertNumber(mimoCbLayers),
                            Utility.convertNumber(mimoNonCbLayers),
                            1
                        )
                    val qam =
                        it.getString("supportedModulationOrderUL")?.removePrefix("qam").plus("qam")
                    FeaturePerCCNr(
                        type = FeatureSet.UPLINK,
                        mimo = mimo,
                        qam = qam,
                        scs = scs,
                        bw = bw,
                        channelBW90mhz = channelBW90mhz
                    )
                }

            uplink =
                featureSets.getArray("featureSetsUplink")?.mapNotNull { featureSetPerCCList ->
                    val list =
                        featureSetPerCCList.getArray("featureSetListPerUplinkCC")?.mapNotNull {
                            index ->
                            (index as? JsonPrimitive)?.intOrNull?.let {
                                // NR PerCC-ID is 1..1024 while LTE PerCC-ID is 0..32
                                uplinkPerCC?.getOrNull(it - 1)
                            }
                        }
                    FeatureSet(list, FeatureSet.UPLINK)
                }
                    ?: uplink
        }

        if (debug) {
            println("\nNr FeatureSets")
            println(downlink.joinToString(separator = "\n"))
            println(uplink.joinToString(separator = "\n"))
        }
        return FeatureSets(downlink, uplink)
    }

    private infix fun IntRange.step(next: (Int) -> Int) =
        generateSequence(first, next).takeWhile { if (first < last) it <= last else it >= last }
    private fun Map<Int, IntArray>.merge(map: Map<Int, IntArray>): MutableMap<Int, IntArray> {
        val mutableMap = this.toMutableMap()
        map.entries.forEach { (key, value) ->
            mutableMap[key] = mutableMap[key]?.plus(value) ?: value
        }
        return mutableMap
    }
}
