package it.smartphonecombo.uecapabilityparser.importer.ltenr

import it.smartphonecombo.uecapabilityparser.Utility
import it.smartphonecombo.uecapabilityparser.Utility.toBwString
import it.smartphonecombo.uecapabilityparser.bean.*
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.lte.FeaturePerCCLte
import it.smartphonecombo.uecapabilityparser.bean.nr.BwTableNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.bean.nr.FeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class ImportUECapabilityInformation : ImportCapabilities {
    private val comboList = Capabilities()
    protected var supportBandCombination = "SupportedBandCombination-r10"
    protected var supportBandCombinationAdd = "SupportedBandCombinationAdd-r11"
    protected var supportBandCombinationReduced = "supportedBandCombinationReduced-r13"
    protected var mimo4BandCombination = "supportedMIMO-CapabilityDL-r10: fourLayers"
    private var supportedBandCombination_v1090 = "supportedBandCombination-v1090"
    protected var supportedBandListEUTRA_v9e0: String? = "supportedBandListEUTRA-v9e0"
    private var supportedBandCombination_v1430 = "supportedBandCombination-v1430"
    private var supportedBandCombinationReduced_v1430 = "supportedBandCombinationReduced-v1430"
    private var supportedBandCombinationAdd_v1430 = "supportedBandCombinationAdd-v1430"
    private var supportedBandCombination_v1530 = "supportedBandCombination-v1530"
    private var supportedBandCombinationReduced_v1530 = "supportedBandCombinationReduced-v1530"
    private var supportedBandCombinationAdd_v1530 = "supportedBandCombinationAdd-v1530"
    private var supportedBandListEUTRA_v1250 = "supportedBandListEUTRA-v1250"
    private var supportedBandListEN_DC_r15 = "supportedBandListEN-DC-r15"
    private var supportedBandListNR_SA_r15 = "supportedBandListNR-SA-r15"
    private var supportedBandListNR = "supportedBandListNR"
    private var appliedFreqBandListFilter = "appliedFreqBandListFilter"
    private var rat_type_eutra = "eutra"
    private var rat_type_eutra_nr = "eutra-nr"
    private var rat_type_nr = "nr"
    private var featureSetsDL = "featureSetsDL-r15"
    private var featureSetsUL = "featureSetsUL-r15"
    private var featureSetsDLPerCC = "featureSetsDL-PerCC-r15"
    private var featureSetsULPerCC = "featureSetsUL-PerCC-r15"
    private var pdcp_ParametersNR_r15 = "pdcp-ParametersNR-r15"
    private var ratTypeIndex: LinkedHashMap<String, Range>? = null
    private var matcherBandCombination: Matcher? = null
    private var matcherMimo: Matcher? = null
    private var matcherBCS: Matcher? = null
    private var matcherAdd: Matcher? = null
    private var matcherBandsExt: Matcher? = null
    private var matcherReduced: Matcher? = null
    private var matcherSingleBands: Matcher? = null
    private var matcherSingleBandsExt: Matcher? = null
    private var matcherSingleBandsQam: Matcher? = null
    private var matcherQam256ul: Matcher? = null
    private var matcherQam1024dl: Matcher? = null
    private var matcherNSA: Matcher? = null
    private var matcherSA: Matcher? = null
    private var matcherCategory: Matcher? = null

    /**
     * Convert to java class.
     *
     * @param caBandCombosString the ca band combos string
     * @return the combo list
     * @see ImportCapabilities.parse
     */
    override fun parse(caBandCombosString: String): Capabilities {
        ratTypeIndex = getRatTypeIndexes(caBandCombosString)
        initializeEutraMatchers(caBandCombosString)
        setLTECategory()
        val listBands = lteBands
        val nrNSAbands = getNrbands(matcherNSA)
        nrNSAbands.sortWith(Comparator.comparing { obj: ComponentNr -> obj.band })
        comboList.nrNSAbands = nrNSAbands
        val nrSAbands = getNrbands(matcherSA)
        nrSAbands.sortWith(Comparator.comparing { obj: ComponentNr -> obj.band })
        comboList.nrSAbands = nrSAbands
        val listCombo = ArrayList<ComboLte>()
        if (matcherBandCombination != null) {
            parseBandCombination(listBands, listCombo)
        }
        val reduced = matcherAdd == null && matcherReduced != null
        if (reduced || matcherAdd != null) {
            parseBandCombinationAdd(listBands, listCombo, reduced)
        }
        comboList.lteCombos = listCombo
        val listBand: List<ComponentLte> =
            listBands.values.sortedWith(Comparator.comparing { obj: ComponentLte -> obj.band })
        comboList.lteBands = listBand
        val lteFeatures = importLteFeatureset(caBandCombosString)
        val nrFeatures = importNrFeatureset(caBandCombosString)
        val enDC = parseNR(caBandCombosString, rat_type_eutra_nr, lteFeatures, nrFeatures)
        comboList.enDcCombos = enDC
        val nrSA = parseNR(caBandCombosString, rat_type_nr, null, nrFeatures)
        comboList.nrCombos = nrSA
        val nrBands = parseNRbands(caBandCombosString)
        val allCombos: MutableList<ComboNr> = ArrayList(enDC)
        allCombos.addAll(nrSA)
        addOptionalBWs(nrBands, allCombos)
        comboList.nrBands = nrBands
        if (debug) {
            nrBands.forEach { println(it.toBwString()) }
        }
        return comboList
    }

    private fun getRatTypeIndexes(uecapability: String): LinkedHashMap<String, Range> {
        val hashmap = LinkedHashMap<String, Range>()
        val regex = "rat-type ?: ?([\\w-]*)"
        val temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcherRats = temp.matcher(uecapability)
        if (matcherRats.find()) {
            var rat = matcherRats.group(1).lowercase()
            var start = matcherRats.end()
            while (matcherRats.find()) {
                val nextRat = matcherRats.group(1).lowercase()
                val end = matcherRats.end()
                hashmap[rat] = Range(start, end)
                rat = nextRat
                start = end
            }
            hashmap[rat] = Range(
                start,
                uecapability.length
            )
        }
        return hashmap
    }

    private fun initializeEutraMatchers(caBandCombosString: String) {
        if (!ratTypeIndex!!.containsKey(rat_type_eutra)) {
            return
        }
        val end = ratTypeIndex!![rat_type_eutra]!!.end
        var index = caBandCombosString.indexOf(supportBandCombination, ignoreCase = true)
        var regex = regexSupportedBandCombination
        if (index >= 0 && regex.isNotEmpty()) {
            var temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            matcherBandCombination = temp.matcher(caBandCombosString).region(
                index,
                end
            )
            regex = regexSupportedBandCombinationExt
            if (regex.isNotEmpty()) {
                temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                matcherBCS = temp.matcher(caBandCombosString).region(index, end)
            }
            regex = regexCA_MIMO_ParametersDL
            if (regex.isNotEmpty()
                && !caBandCombosString.contains( // Workaround Samsung S20 5G (exynos) bug
                    mimo4BandCombination, ignoreCase = true
                )
            ) {
                temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                matcherMimo = temp.matcher(caBandCombosString)
            }
            regex = regexBandCombinationParameters_v1090
            if (regex.isNotEmpty()) {
                index = caBandCombosString.indexOf(
                    supportedBandCombination_v1090,
                    ignoreCase = true
                )
                if (index >= 0) {
                    temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                    matcherBandsExt = temp.matcher(caBandCombosString).region(index, end)
                }
            }
            regex = regexSupportedBandCombinationAdd
            if (regex.isNotEmpty()) {
                index = caBandCombosString.indexOf(
                    supportBandCombinationAdd,
                    ignoreCase = true
                )
                if (index >= 0) {
                    temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                    matcherAdd = temp.matcher(caBandCombosString).region(index, end)
                }
            }
            regex = regexSupportedBandCombination_v1430
            if (regex.isNotEmpty()) {
                index = caBandCombosString.indexOf(supportedBandCombination_v1430, ignoreCase = true)
                if (index < 0) {
                    index = caBandCombosString.indexOf(supportedBandCombinationAdd_v1430, ignoreCase = true)
                }
                if (index >= 0) {
                    temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                    matcherQam256ul = temp.matcher(caBandCombosString).region(index, end)
                }
            }
            regex = regexSupportedBandCombination_v1530
            if (regex.isNotEmpty()) {
                index = caBandCombosString.indexOf(supportedBandCombination_v1530, ignoreCase = true)
                if (index < 0) {
                    index = caBandCombosString.indexOf(supportedBandCombinationAdd_v1530, ignoreCase = true)
                }
                if (index >= 0) {
                    temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                    matcherQam1024dl = temp.matcher(caBandCombosString).region(
                        index,
                        end
                    )
                }
            }
        }
        regex = regexBandCombinationReduced
        if (regex.isNotEmpty()) {
            index = caBandCombosString.indexOf(supportBandCombinationReduced, ignoreCase = true)
            if (index >= 0) {
                var temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                matcherReduced = temp.matcher(caBandCombosString)
                regex = regexSupportedBandCombination_v1430
                if (regex.isNotEmpty()) {
                    index = caBandCombosString.indexOf(supportedBandCombinationReduced_v1430, ignoreCase = true)
                    if (index >= 0) {
                        temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                        matcherQam256ul = temp.matcher(caBandCombosString).region(
                            index,
                            end
                        )
                    }
                }
                regex = regexSupportedBandCombination_v1530
                if (regex.isNotEmpty()) {
                    index = caBandCombosString.indexOf(supportedBandCombinationReduced_v1530, ignoreCase = true)
                    if (index >= 0) {
                        temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                        matcherQam1024dl = temp.matcher(caBandCombosString).region(
                            index,
                            end
                        )
                    }
                }
            }
        }
        regex = regexSingleBands
        if (regex.isNotEmpty()) {
            var temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            matcherSingleBands = temp.matcher(caBandCombosString)
            regex = regexSupportedBandListEUTRA_v9e0
            if (regex.isNotEmpty()) {
                index = caBandCombosString.indexOf(supportedBandListEUTRA_v9e0!!, ignoreCase = true)
                if (index >= 0) {
                    temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                    matcherSingleBandsExt = temp.matcher(caBandCombosString).region(
                        index,
                        end
                    )
                }
            }
            regex = regexSupportedBandListEUTRA_v1250
            if (regex.isNotEmpty()) {
                index = caBandCombosString.indexOf(supportedBandListEUTRA_v1250, ignoreCase = true)
                if (index > 0) {
                    temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                    matcherSingleBandsQam = temp.matcher(caBandCombosString).region(
                        index,
                        end
                    )
                }
            }
        }
        regex = regexSupportedBandListEN_DC
        if (regex.isNotEmpty()) {
            var temp: Pattern
            index = caBandCombosString.indexOf(supportedBandListEN_DC_r15, ignoreCase = true)
            val index2 = caBandCombosString.indexOf(supportedBandListNR_SA_r15, ignoreCase = true)
            if (index >= 0) {
                temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                matcherNSA = temp.matcher(caBandCombosString)
                if (index2 >= 0 && index2 > index) {
                    matcherNSA!!.region(index, index2)
                } else {
                    matcherNSA!!.region(index, end)
                }
            }
            regex = regexSupportedBandListNR_SA
            if (index2 >= 0 && regex.isNotEmpty()) {
                temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                matcherSA = temp.matcher(caBandCombosString).region(index2, end)
            }
        }
        regex = regexUECategory
        if (regex.isNotEmpty()) {
            val temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            matcherCategory = temp.matcher(caBandCombosString)
        }
    }

    private fun setLTECategory() {
        var dlCategory = 0
        var ulCategory = 0
        if (matcherCategory != null) {
            while (matcherCategory!!.find()) {
                try {
                    val value = matcherCategory!!.group(2).toInt()
                    val direction = matcherCategory!!.group(1)
                    if (direction == null) {
                        ulCategory = value
                        dlCategory = ulCategory
                        continue
                    }
                    when (direction) {
                        "DL" -> dlCategory = value
                        "UL" -> ulCategory = value
                        else -> {
                            ulCategory = value
                            dlCategory = ulCategory
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        comboList.lteCategoryDL = dlCategory
        comboList.lteCategoryUL = ulCategory
    }

    private val lteBands: HashMap<Int, ComponentLte>
        get() {
            val listBands = HashMap<Int, ComponentLte>()
            if (matcherSingleBands != null) {
                var i = 0
                while (matcherSingleBands!!.find()) {
                    try {
                        val baseBand = matcherSingleBands!!.group(2).toInt()
                        val band = ComponentLte(baseBand, 'A', 2)
                        if (matcherSingleBandsExt != null && matcherSingleBandsExt!!.find()) {
                            val bandsExt: Int = try {
                                matcherSingleBandsExt!!.group(1).toInt()
                            } catch (ex: NumberFormatException) {
                                i
                            }
                            if (bandsExt == i && band.band == 64) {
                                val result = matcherSingleBandsExt!!.group(2)
                                if (result != null) {
                                    band.band = result.toInt()
                                }
                            }
                        }
                        try {
                            if (matcherSingleBandsQam != null && matcherSingleBandsQam!!.find()) {
                                if (matcherSingleBandsQam!!.group(2) == "supported") {
                                    band.modDL = "256qam"
                                }
                                if (matcherSingleBandsQam!!.group(3) != null
                                    && matcherSingleBandsQam!!.group(3) == "supported"
                                ) {
                                    band.modUL = "64qam"
                                }
                            }
                        } catch (ignored: Exception) {
                        }
                        listBands[band.band] = band
                        i++
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
            return listBands
        }

    private fun getNrbands(matcher: Matcher?): MutableList<ComponentNr> {
        val componentNrs: MutableList<ComponentNr> = ArrayList()
        if (matcher != null) {
            while (matcher.find()) {
                try {
                    val band = ComponentNr(matcher.group(2).toInt())
                    componentNrs.add(band)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        return componentNrs
    }

    private fun parseBandCombination(listBands: HashMap<Int, ComponentLte>, listCombo: ArrayList<ComboLte>) {
        var count = 0
        var bcsIndex = 0
        var mimoIndex = 0
        var Qam256ulIndex = 0
        var Qam1024dlIndex = 0
        var resetBcs = false
        var resetMimo = false
        val matcher = matcherBandCombination
        while (matcher!!.find()) {
            try {
                count = matcher.group(1).toInt()
            } catch (ex: NumberFormatException) {
                count++
            }
            val bands = ArrayList<IComponent>()
            var i = 2
            while (i <= matcher.groupCount() - 3
                && matcher.group(i) != null
            ) {
                val baseBand = matcher.group(i++).toInt()
                var dlMod: String?
                var ulMod: String?
                val uplink: Char = try {
                    matcher.group(i++).uppercase()[0]
                } catch (ex: NullPointerException) {
                    '0'
                }
                val bandwidthClass = matcher.group(i++).uppercase()[0]
                val mimo: Int = try {
                    Utility.convertNumber(matcher.group(i++))
                } catch (ex: NullPointerException) {
                    0
                }
                // Basic Mod
                val temp = listBands.getOrDefault(baseBand, ComponentLte())
                dlMod = temp.modDL
                ulMod = temp.modUL
                if (dlMod == "1024qam") {
                    // Avoid advanced modulation
                    dlMod = "256qam"
                }
                if (ulMod == "256qam") {
                    // Avoid advanced modulation
                    ulMod = "64qam"
                }
                bands.add(
                    ComponentLte(
                        baseBand, bandwidthClass, uplink, mimo, dlMod,
                        ulMod
                    )
                )
            }
            if (matcherMimo != null) {
                val res: Boolean = if (resetMimo) {
                    matcherMimo!!.find(mimoIndex)
                } else {
                    matcherMimo!!.find()
                }
                if (res) {
                    if (matcherMimo!!.group(1).toInt() == count) {
                        var j = 0
                        var o = 2
                        while (j < bands.size) {
                            val result = matcherMimo!!.group(j + o + 1)
                            if (result != null && result == "supported") {
                                bands[j].mimoDL = 4
                            }
                            j++
                            o++
                        }
                        resetMimo = false
                    } else {
                        resetMimo = true
                        mimoIndex = matcherMimo!!.start()
                    }
                }
            }

            if (matcherBandsExt != null && matcherBandsExt!!.find()) {
                val bandsExt: Int = try {
                    matcherBandsExt!!.group(1).toInt()
                } catch (ex: NumberFormatException) {
                    count
                }
                if (bandsExt == count) {
                    for (j in bands.indices) {
                        val result = matcherBandsExt!!.group(j + 2)
                        if (result != null) {
                            val band: Int = try {
                                result.toInt()
                            } catch (ex: NumberFormatException) {
                                64
                            }
                            val extBand = bands[j]
                            extBand.band = band
                            // Re-set basic mod
                            listBands[band]?.let {
                                extBand.modDL = it.modDL
                                extBand.modUL = it.modUL
                            }
                        }
                    }
                }
            }

            if (matcherQam256ul != null) {
                Qam256ulIndex = setAdvancedModulation(bands, listBands, count, Qam256ulIndex, add = false, dl = false)
            }
            if (matcherQam1024dl != null) {
                Qam1024dlIndex = setAdvancedModulation(bands, listBands, count, Qam1024dlIndex, add = false, dl = true)
            }

            // Update mimo in listBands
            if (listBands.size > 0 && bands.size == 1) {
                val band = bands[0]
                if (listBands.containsKey(band.band)) {
                    if (band.mimoDL > 2) {
                        listBands[band.band]?.mimoDL = band.mimoDL
                    }
                    if (band.classUL != '0') {
                        listBands[band.band]?.classUL = 'A'
                    }
                }
            }
            bands.sortWith(IComponent.defaultComparator.reversed())
            val bandArray = bands.toTypedArray()
            var bcs = 0
            val result: Boolean = if (resetBcs) {
                matcherBCS!!.find(bcsIndex)
            } else {
                matcherBCS!!.find()
            }
            if (result) {
                val bcsComboNumber: Int = try {
                    matcherBCS!!.group(1).toInt()
                } catch (ex: NumberFormatException) {
                    count
                }
                if (bcsComboNumber == count) {
                    try {
                        val bcsString = matcherBCS!!.group(2)
                        var bcsIsHex = true
                        if (bcsString != null) {
                            if (matcherBCS!!.groupCount() > 2) {
                                val res = matcherBCS!!.group(3)
                                if (res != null && res.equals("B", ignoreCase = true)) {
                                    bcsIsHex = false
                                }
                            }
                            bcs = Utility.convertBCStoInt(bcsString, bcsIsHex)
                        }
                        resetBcs = false
                    } catch (ignored: Exception) {
                    }
                } else {
                    resetBcs = true
                    bcsIndex = matcherBCS!!.start()
                }
            }
            listCombo.add(ComboLte(bandArray, bcs))
        }
    }

    private fun parseBandCombinationAdd(
        listBands: HashMap<Int, ComponentLte>,
        listCombo: ArrayList<ComboLte>,
        reduced: Boolean
    ) {
        var count = 0
        var Qam256ulIndex = try {
            matcherQam256ul!!.end()
        } catch (_: Exception) {
            0
        }
        var Qam1024dlIndex = try {
            matcherQam1024dl!!.end()
        } catch (_: Exception) {
            0
        }
        val matcher = if (reduced) matcherReduced else matcherAdd
        while (matcher!!.find()) {
            val bands = ArrayList<IComponent>()
            var i = 2
            while (i <= matcher.groupCount() - 4
                && matcher.group(i) != null
            ) {
                val baseBand = matcher.group(i++).toInt()
                var dlMod: String?
                var ulMod: String?
                val uplink: Char = try {
                    matcher.group(i++).uppercase()[0]
                } catch (ex: NullPointerException) {
                    '0'
                }
                val bandwidthClass = matcher.group(i++).uppercase()[0]
                var mimo: Int = Utility.convertNumber(matcher.group(i++))

                // Some devices only reports fourLayerTM3-TM4-r13
                if (reduced && matcher.group(i++) == "supported" && mimo < 4) {
                    mimo = 4
                }

                // Some devices don't report supportedMIMO-CapabilityDL for twoLayers
                if (mimo == 0 && bandwidthClass != '0') {
                    mimo = 2
                }

                // Basic Mod
                val temp = listBands.getOrDefault(baseBand, ComponentLte())
                dlMod = temp.modDL
                ulMod = temp.modUL
                if (dlMod == "1024qam") {
                    // Avoid advanced modulation
                    dlMod = "256qam"
                }
                if (ulMod == "256qam") {
                    // Avoid advanced modulation
                    ulMod = "64qam"
                }

                // Update mimo only for bandCombination Reduced
                if (reduced) {
                    // Update mimo in listBands
                    if (listBands.size > 0 && listBands.containsKey(baseBand)) {
                        if (mimo > 2) {
                            listBands[baseBand]?.mimoDL = mimo
                        }
                        if (uplink != '0') {
                            listBands[baseBand]?.classUL = 'A'
                        }
                    }
                }
                bands.add(
                    ComponentLte(
                        baseBand, bandwidthClass, uplink, mimo, dlMod,
                        ulMod
                    )
                )
            }

            if (!reduced && matcherMimo != null && matcherMimo!!.find()) {
                if (matcherMimo!!.group(1).toInt() == count) {
                    var j = 0
                    var o = 2
                    while (j < bands.size) {
                        val result = matcherMimo!!.group(j + o + 1)
                        if (result != null && result == "supported") {
                            bands[j].mimoDL = 4
                        }
                        j++
                        o++
                    }
                }
            }

            if (matcherQam256ul != null) {
                Qam256ulIndex = setAdvancedModulation(bands, listBands, count, Qam256ulIndex, add = true, dl = false)
            }
            if (matcherQam1024dl != null) {
                Qam1024dlIndex = setAdvancedModulation(bands, listBands, count, Qam1024dlIndex, add = true, dl = true)
            }
            bands.sortWith(IComponent.defaultComparator.reversed())
            val bandArray = bands.toTypedArray()
            var bcs = 0
            val bcsString = matcher.group("bcs")
            if (bcsString != null) {
                var bcsIsHex = true
                try {
                    try {
                        val res = matcher.group("bcsUnit")
                        if (res != null && res.equals("B", ignoreCase = true)) {
                            bcsIsHex = false
                        }
                    } catch (ignored: IllegalArgumentException) {
                    }
                    bcs = Utility.convertBCStoInt(bcsString, bcsIsHex)
                } catch (ignored: Exception) {
                }
            }
            listCombo.add(ComboLte(bandArray, bcs))
            count++
        }
    }

    private fun importLteFeatureset(caBandCombosString: String): FeatureSets {
        val downlink = lteFeaturesPerCC(caBandCombosString, true)
        val uplink = lteFeaturesPerCC(caBandCombosString, false)
        if (debug) {
            println("\nLTE FeatureSets")
            println(downlink)
            println(uplink)
        }
        return FeatureSets(downlink, uplink)
    }

    private fun importNrFeatureset(caBandCombosString: String): FeatureSets {
        val regex = regexNRFeatureSetPerCC
        if (!ratTypeIndex!!.containsKey(rat_type_nr) || regex.isEmpty()) {
            return FeatureSets(ArrayList(), ArrayList())
        }
        val ratTypeNrRange = ratTypeIndex!![rat_type_nr]
        val index = ratTypeNrRange!!.start
        val end = ratTypeNrRange.end
        var matcherPerCC: Matcher
        var mainMatcher: Matcher
        run {
            var temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            matcherPerCC = temp.matcher(caBandCombosString).region(index, end)
            temp = Pattern
                .compile(regexNRFeatureSetPerCCList, Pattern.CASE_INSENSITIVE)
            mainMatcher = temp.matcher(caBandCombosString).region(index, end)
        }
        val downlinkPerCC: MutableList<FeaturePerCCNr> = ArrayList()
        val uplinkPerCC: MutableList<FeaturePerCCNr> = ArrayList()
        var typeDL = true
        while (matcherPerCC.find()) {
            val temp = FeaturePerCCNr()
            temp.scs = matcherPerCC.group(2).toInt()
            temp.bw = matcherPerCC.group(3).toInt()
            if (matcherPerCC.group(4) != null && matcherPerCC.group(4).equals("supported", ignoreCase = true)) {
                temp.channelBW90mhz = true
            }
            temp.mimo = Utility.convertNumber(matcherPerCC.group(5))

            val modulation = matcherPerCC.group(6).lowercase()
            temp.qam = if (modulation.startsWith("qam")) {
                modulation.removePrefix("qam") + "qam"
            } else {
                modulation
            }

            if (matcherPerCC.group(1) != null) typeDL = matcherPerCC.group(1) == "Downlink"
            if (typeDL) {
                temp.type = FeaturePerCCNr.DOWNlINK
                downlinkPerCC.add(temp)
            } else {
                temp.type = FeaturePerCCNr.UPLINK
                uplinkPerCC.add(temp)
            }
        }
        val downlink: MutableList<FeatureSet> = ArrayList()
        val uplink: MutableList<FeatureSet> = ArrayList()
        typeDL = true
        while (mainMatcher.find()) {
            var i = 4
            val featureSets: MutableList<FeaturePerCCNr> = ArrayList()
            if (mainMatcher.group(1) != null) typeDL = mainMatcher.group(1) == "Downlink"
            if (typeDL) {
                val temp = FeatureSet(featureSets, FeatureSet.DOWNlINK)
                while (i <= mainMatcher.groupCount() && mainMatcher.group(i) != null) {
                    featureSets.add(
                        downlinkPerCC[mainMatcher.group(i).toInt() - 1]
                    )
                    i += 2
                }
                downlink.add(temp)
            } else {
                val temp = FeatureSet(featureSets, FeatureSet.UPLINK)
                while (i <= mainMatcher.groupCount() && mainMatcher.group(i) != null) {
                    featureSets.add(
                        uplinkPerCC[mainMatcher.group(i).toInt() - 1]
                    )
                    i += 2
                }
                uplink.add(temp)
            }
        }
        if (debug) {
            println("\nNR FeatureSets")
            println(downlinkPerCC)
            println(uplinkPerCC)
            println(downlink)
            println(uplink)
        }
        return FeatureSets(downlink, uplink)
    }

    private fun parseNR(
        caBandCombosString: String, rat_type: String, lteFeatures: FeatureSets?,
        nrFeatures: FeatureSets
    ): List<ComboNr> {
        val listCombo = ArrayList<ComboNr>()
        val regex = regexNrCombos
        if (!ratTypeIndex!!.containsKey(rat_type) || regex.isEmpty()) {
            return listCombo
        }
        var matcher: Matcher
        var featureMatcher: Matcher
        val ratTypeRange = ratTypeIndex!![rat_type]
        val end = ratTypeRange!!.end
        val index = ratTypeRange.start
        run {
            var temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            matcher = temp.matcher(caBandCombosString).region(index, end)
            temp = Pattern
                .compile(regexFeatureSetCombinations, Pattern.CASE_INSENSITIVE)
            featureMatcher = temp.matcher(caBandCombosString).region(index, end)
        }
        while (matcher.find()) {
            val comboNumber = matcher.group(1).toInt()
            if (debug) {
                print("found combo $comboNumber:\t\t\t")
            }
            val bands = ArrayList<IComponent>()
            val nrbands = ArrayList<IComponent>()
            var i = 2
            while (matcher.group(i) != null) {
                if (matcher.group(i++) == "eutra") {
                    val baseBand = matcher.group(i++).toInt()
                    val bandwidthClass = matcher.group(i++).uppercase()[0]
                    val uplink: Char = try {
                        matcher.group(i++).uppercase()[0]
                    } catch (ex: NullPointerException) {
                        '0'
                    }
                    bands.add(ComponentLte(baseBand, bandwidthClass, uplink, 0, "64qam", "16qam"))
                } else {
                    val nrband = ComponentNr(matcher.group(i++).toInt())
                    val bandwidthClass: Char = try {
                        matcher.group(i++).uppercase()[0]
                    } catch (ex: NullPointerException) {
                        '0'
                    }
                    val uplink: Char = try {
                        matcher.group(i++).uppercase()[0]
                    } catch (ex: NullPointerException) {
                        '0'
                    }
                    nrband.classDL = bandwidthClass
                    nrband.classUL = uplink
                    nrbands.add(nrband)
                }
            }
            val bandArray = bands.toTypedArray()
            val nrbandsArray = nrbands.toTypedArray()
            val featureSet = matcher.group("featureset").toInt()
            val newCombo = if (bandArray.isEmpty()) {
                ComboNr(nrbandsArray, featureSet)
            } else {
                ComboNr(bandArray, nrbandsArray, featureSet)
            }
            listCombo.add(newCombo)
            if (debug) {
                println(newCombo)
            }
        }
        val features = ArrayList<ArrayList<ArrayList<Feature>>>()
        while (featureMatcher.find()) {
            val comboNumber = featureMatcher.group(1).toInt()
            if (debug) {
                print("found feature $comboNumber: \t\t")
            }
            var i = 2
            val featuresInt = ArrayList<ArrayList<Feature>>()
            var ccIndex = 0
            while (featureMatcher.group(i) != null) {
                val list = ArrayList<Feature>()
                while (featureMatcher.group(i) != null) {
                    val feature = Feature(
                        featureMatcher.group(i++) == "nr",
                        featureMatcher.group(i++).toInt(),
                        featureMatcher.group(i++).toInt()
                    )
                    list.add(feature)
                }
                featuresInt.add(list)
                if (++ccIndex >= ImportCapabilities.nrDlCC) {
                    break
                }
                // The real max is 128 (not 32), but that would be too slow...
                while (++i < 32 * 3 * ccIndex + 2);
            }
            if (debug) {
                println(featuresInt)
            }
            features.add(featuresInt)
        }
        return linkFeaturesAndCarrier(listCombo, features, lteFeatures, nrFeatures)
    }

    private fun parseNRbands(uecapabilityinfo: String): List<ComponentNr> {
        val nrBands: MutableList<ComponentNr> = ArrayList()
        val regex = regexSupportedBandListNR
        if (!ratTypeIndex!!.containsKey(rat_type_nr) || regex.isEmpty()) {
            return nrBands
        }
        val temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val ratTypeRange = ratTypeIndex!![rat_type_nr]
        val end = ratTypeRange!!.end
        val index = ratTypeRange.start
        val matcher = temp.matcher(uecapabilityinfo).region(index, end)
        while (matcher.find()) {
            val nrBand = ComponentNr(matcher.group(2).toInt())
            if (nrBand.isFR2 && !"supported".equals(matcher.group(3), ignoreCase = true)) {
                nrBand.modDL = "64qam"
            }
            if ("supported".equals(matcher.group(4), ignoreCase = true)) {
                nrBand.modUL = "256qam"
            }
            if ("supported".equals(matcher.group(6), ignoreCase = true)) {
                nrBand.rateMatchingLTEcrs = true
            }
            var result = matcher.group(5)
            if (result != null) {
                nrBand.powerClass = result.toInt()
            }
            result = matcher.group(20)
            if (result != null) {
                nrBand.maxUplinkDutyCycle = result.toInt()
            }
            parseNRChannelBWs(matcher, nrBand)
            nrBands.add(nrBand)
        }
        return nrBands
    }

    private fun addOptionalBWs(nrBands: List<IComponent>, combos: List<ComboNr>) {
        val optionalBWs: MutableMap<Int, ComponentNr> = HashMap()
        combos.forEach { x: ComboNr ->
            for (y in x.componentsNr) {
                val component = y as ComponentNr
                val band = component.band
                if (!optionalBWs.containsKey(band)) {
                    if (component.channelBW90mhz || component.maxBandwidth == 400) {
                        optionalBWs[band] = y
                    }
                }
            }
        }
        if (optionalBWs.isEmpty()) {
            return
        }
        nrBands.parallelStream().forEach { x: IComponent ->
            val component = x as ComponentNr
            val band = x.band
            if (optionalBWs.containsKey(band)) {
                val nrComponent = optionalBWs[band]
                if (nrComponent != null) {
                    val dlBWs = component.bandwidthsDL
                    val ulBWs = component.bandwidthsUL
                    val optionalBW = if (component.isFR2) 400 else 90
                    val startSCS = if (component.isFR2) 120 else 30
                    val endSCS = nrComponent.scs
                    var scs = startSCS
                    while (scs <= endSCS) {
                        if (dlBWs != null) {
                            if (dlBWs.containsKey(scs)) {
                                dlBWs[scs] = dlBWs[scs]!! + optionalBW
                            }
                        }
                        if (ulBWs != null) {
                            if (ulBWs.containsKey(scs)) {
                                ulBWs[scs] = ulBWs[scs]!! + optionalBW
                            }
                        }
                        scs *= 2
                    }
                }
            }
        }
    }

    protected abstract val regexSupportedBandCombination: String
    protected abstract val regexSupportedBandCombinationExt: String
    protected abstract val regexCA_MIMO_ParametersDL: String
    protected abstract val regexBandCombinationParameters_v1090: String
    protected abstract val regexSupportedBandCombinationAdd: String
    protected abstract val regexSupportedBandCombination_v1430: String
    protected abstract val regexSupportedBandCombination_v1530: String
    protected abstract val regexBandCombinationReduced: String
    protected abstract val regexSingleBands: String
    protected abstract val regexSupportedBandListEUTRA_v9e0: String
    protected abstract val regexSupportedBandListEUTRA_v1250: String
    protected abstract val regexSupportedBandListEN_DC: String
    protected abstract val regexSupportedBandListNR_SA: String
    protected abstract val regexUECategory: String
    private fun setAdvancedModulation(
        bands: ArrayList<IComponent>,
        listBands: Map<Int, ComponentLte>,
        count: Int,
        index: Int,
        add: Boolean,
        dl: Boolean
    ): Int {
        val res: Boolean
        val matcher = if (dl) matcherQam1024dl else matcherQam256ul
        res = if (index > 0) {
            matcher!!.find(index)
        } else {
            matcher!!.find()
        }
        return if (res) {
            val matchCount: Int = try {
                matcher.group(1).toInt()
            } catch (ex: Exception) {
                count
            }
            if (matchCount == count) {
                var j = 0
                var o = 2
                while (j < bands.size) {
                    val result = matcher.group(j + o + 1)
                    if (result != null && result == "supported") {
                        if (dl) {
                            bands[j].modDL = "1024qam"
                        } else {
                            bands[j].modUL = "256qam"
                        }
                        if (listBands.containsKey(bands[j].band)) {
                            if (dl) {
                                listBands[bands[j].band]!!.modDL = "1024qam"
                            } else {
                                listBands[bands[j].band]!!.modUL = "256qam"
                            }
                        }
                    }
                    j++
                    o++
                }
                -1
            } else {
                matcher.start()
            }
        } else {
            -2
        }
    }

    private fun lteFeaturesPerCC(
        caBandCombosString: String,
        dl: Boolean
    ): List<FeatureSet> {
        val featureset: MutableList<FeatureSet> = ArrayList()
        val startString = if (dl) featureSetsDL else featureSetsUL
        val startStringPerCC = if (dl) featureSetsDLPerCC else featureSetsULPerCC
        val index = caBandCombosString.indexOf(startString, ignoreCase = true)
        val endString = if (dl) featureSetsUL else pdcp_ParametersNR_r15
        val indexPerCC = caBandCombosString.indexOf(startStringPerCC, ignoreCase = true)
        var end = caBandCombosString.indexOf(endString, ignoreCase = true)
        if (end < 0 || end < index) end = caBandCombosString.length
        var matcherPerCC: Matcher? = null
        var mainMatcher: Matcher? = null
        val regex = regexLTEFeatureSetPerCC
        if (index >= 0 && !regex.isNullOrEmpty()) {
            var temp = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            matcherPerCC = temp.matcher(caBandCombosString).region(indexPerCC, end)
            temp = Pattern.compile(
                regexLTEFeatureSetPerCCList!!,
                Pattern.CASE_INSENSITIVE
            )
            mainMatcher = temp.matcher(caBandCombosString).region(index, end)
        }
        if (mainMatcher == null) {
            return featureset
        }
        val featuresPerCC: MutableList<FeaturePerCCLte> = ArrayList()
        while (matcherPerCC!!.find()) {
            val temp = FeaturePerCCLte()
            if (matcherPerCC.group(2) != null) {
                temp.mimo = Utility.convertNumber(matcherPerCC.group(2))
            } else {
                temp.mimo = 1
            }
            if (dl) {
                temp.type = FeaturePerCCLte.DOWNlINK
                temp.qam = "256qam"
            } else {
                temp.type = FeaturePerCCLte.UPLINK
                temp.qam = "64qam"
            }
            featuresPerCC.add(temp)
            if (matcherPerCC.group(3) != null) temp.qam = matcherPerCC.group(3).lowercase()
        }
        while (mainMatcher.find()) {
            val temp: FeatureSet
            var i = 4
            val featureSets: MutableList<FeaturePerCCLte> = ArrayList()
            temp = if (dl) {
                FeatureSet(featureSets, FeatureSet.DOWNlINK)
            } else {
                FeatureSet(featureSets, FeatureSet.UPLINK)
            }
            while (i <= mainMatcher.groupCount() && mainMatcher.group(i) != null) {
                featureSets
                    .add(featuresPerCC[mainMatcher.group(i).toInt()])
                i += 2
            }
            featureset.add(temp)
        }
        return featureset
    }

    protected abstract val regexNRFeatureSetPerCC: String
    protected abstract val regexNRFeatureSetPerCCList: String
    protected abstract val regexNrCombos: String
    protected abstract val regexFeatureSetCombinations: String
    private fun linkFeaturesAndCarrier(
        combos: List<ComboNr>,
        featureSets: ArrayList<ArrayList<ArrayList<Feature>>>,
        lteFeatures: FeatureSets?,
        nrFeatures: FeatureSets
    ): List<ComboNr> {
        val list = mutableListOf<ComboNr>()
        for (combo in combos) {
            val featureSet = featureSets.getOrNull(combo.featureSet) ?: continue
            val indices = featureSet.firstOrNull()?.indices ?: IntRange.EMPTY

            for (index in indices) {
                val lteComponents = combo.componentsLte
                val newLteComponents = mutableListOf<IComponent>()
                var j = 0
                if (lteFeatures != null) {
                    while (j < lteComponents.size) {
                        try {
                            val oldComponentLte = lteComponents[j]
                            val band = ComponentLte(
                                oldComponentLte.band,
                                oldComponentLte.classDL,
                                oldComponentLte.classUL
                            )
                            if (featureSet[j][index].downlink == 0) {
                                // fallback combo
                                j++
                                continue
                            }
                            val downLinkFeature =
                                lteFeatures.downlink?.get(featureSet[j][index].downlink - 1)
                                    ?.featureSetsPerCC?.get(0)
                            if (downLinkFeature != null) {
                                band.mimoDL = downLinkFeature.mimo
                            }
                            if (featureSet[j][index].uplink != 0) {
                                val uplinkFeature =
                                    lteFeatures.uplink?.get(featureSet[j][index].uplink - 1)?.featureSetsPerCC?.get(
                                        0
                                    )
                                if (uplinkFeature != null) {
                                    band.modUL = uplinkFeature.qam
                                }
                            }
                            newLteComponents.add(band)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                        j++
                    }
                }
                val nrComponents = combo.componentsNr
                val newNrComponents = mutableListOf<IComponent>()
                for (m in nrComponents.indices) {
                    val oldComponent = nrComponents[m] as ComponentNr
                    val nrComponent = ComponentNr(
                        oldComponent.band,
                        oldComponent.classDL,
                        oldComponent.classUL
                    )
                    try {
                        val downLinkFeature =
                            nrFeatures.downlink?.get(featureSet[j + m][index].downlink - 1)?.featureSetsPerCC?.get(
                                0
                            ) as FeaturePerCCNr?
                        if (downLinkFeature != null) {
                            nrComponent.mimoDL = downLinkFeature.mimo
                            nrComponent.modDL = downLinkFeature.qam
                            nrComponent.maxBandwidth = downLinkFeature.bw
                            if (downLinkFeature.bw >= 80) {
                                nrComponent.channelBW90mhz = downLinkFeature.channelBW90mhz
                            }
                            nrComponent.scs = downLinkFeature.scs
                        }
                    } catch (ex: Exception) {
                        // ex.printStackTrace();
                    }
                    val uplink = featureSet[j + m][index].uplink
                    if (uplink > 0) {
                        val uplinkFeature =
                            nrFeatures.uplink?.get(uplink - 1)?.featureSetsPerCC?.get(0) as FeaturePerCCNr?

                        if (uplinkFeature != null) {
                            nrComponent.mimoUL = uplinkFeature.mimo
                            nrComponent.modUL = uplinkFeature.qam
                            if (uplinkFeature.mimo == 0) {
                                nrComponent.mimoUL = 1
                            }
                        }
                    }
                    newNrComponents.add(nrComponent)
                }

                // Sort
                val lteArray = newLteComponents
                    .sortedWith(IComponent.defaultComparator.reversed())
                    .toTypedArray()
                val nrArray = newNrComponents
                    .sortedWith(IComponent.defaultComparator.reversed())
                    .toTypedArray()

                val comboNr = if (newLteComponents.isNotEmpty()) {
                    ComboNr(lteArray, nrArray, combo.featureSet)
                } else {
                    ComboNr(nrArray, combo.featureSet)
                }

                list.add(comboNr)
            }
        }
        return list
    }

    protected abstract val regexSupportedBandListNR: String
    private fun parseNRChannelBWs(matcher: Matcher, nrBand: ComponentNr) {
        val band = nrBand.band
        val bandwidthsDL = HashMap<Int, IntArray>()
        val bandwidthsUL = HashMap<Int, IntArray>()
        var i = 7
        while (i < 33) {
            val result = matcher.group(i)
            if (result == null || i == 19) {
                i += 2
                continue
            }
            val scs = result.toInt()
            val bwstring = matcher.group(i + 1)
            var bws = Utility.bwStringToArray(bwstring, nrBand.isFR2, i >= 19)

            /*
             * According to TS 38.306 v16.6.0 there's no 100MHz field for n41, n48, n77, n78, n79, n90
             * So we assume that it's supported by default.
             */
            if (scs != 15 && bws.isNotEmpty() && (band == 41 || band == 48 || band in 77..79 || band == 90) && i < 19) {
                bws += 100
            }
            if (i < 13) {
                bandwidthsDL[scs] = bws
            } else if (i < 19) {
                bandwidthsUL[scs] = bws
            } else if (i < 27) {
                val oldBWs = bandwidthsDL.getOrDefault(scs, IntArray(0))
                bandwidthsDL[scs] = oldBWs + bws
            } else {
                val oldBWs = bandwidthsUL.getOrDefault(scs, IntArray(0))
                bandwidthsUL[scs] = oldBWs + bws
            }
            i += 2
        }


        /*
         * According to TS 38.306 v16.6.0, the UE can omit channelBWs or specific SCS in channelBWs
         * if it supports all BWs defined in 38.101-1 and 38.101-2 v15.7.0.
         * So we add default BWs here when a specific SCS is missing.
         */
        var startSCS = 15
        var endSCS = 60
        if (nrBand.isFR2) {
            startSCS = 60
            endSCS = 120
        }
        var scs = startSCS
        while (scs <= endSCS) {
            val bw = BwTableNr.getDLBws(nrBand.band, scs)
            if (!bandwidthsDL.containsKey(scs)) {
                bandwidthsDL[scs] = bw.bwsDL
            }
            if (!bandwidthsUL.containsKey(scs)) {
                bandwidthsUL[scs] = bw.bwsUL
            }
            scs *= 2
        }
        nrBand.bandwidthsDL = bandwidthsDL
        nrBand.bandwidthsUL = bandwidthsUL
    }

    protected abstract val regexLTEFeatureSetPerCC: String?
    protected abstract val regexLTEFeatureSetPerCCList: String?

    class Range(
        val start: Int,
        val end: Int
    )
}