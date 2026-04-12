package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity
import it.smartphonecombo.uecapabilityparser.io.InputSource
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.combo.ComboEnDc
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNr
import it.smartphonecombo.uecapabilityparser.model.combo.ComboNrDc
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.ComponentLte
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.model.feature.FeaturePerCCLte
import it.smartphonecombo.uecapabilityparser.model.feature.FeaturePerCCNr
import it.smartphonecombo.uecapabilityparser.model.feature.FeatureSet
import it.smartphonecombo.uecapabilityparser.model.feature.IFeaturePerCC
import it.smartphonecombo.uecapabilityparser.model.modulation.ModulationOrder
import it.smartphonecombo.uecapabilityparser.model.toMimo

/**
 * A parser for MediaTek 5G modem trace messages logs.
 *
 * Parses NR-CA and EN-DC combo information from NRRC capability trace messages, including NR DL/UL
 * FSpCC definitions, EUTRA DL/UL FSpCC definitions, Feature Set to FSpCC mappings, Feature Set
 * Combinations (FSC), and combo band combinations.
 *
 * The output is a [Capabilities] with NR-CA combos in [nrCombos][Capabilities.nrCombos], EN-DC
 * combos in [enDcCombos][Capabilities.enDcCombos], and NR-DC combos in
 * [nrDcCombos][Capabilities.nrDcCombos].
 */
object ImportMtkNr : ImportCapabilities {

    // Optional [MAIN] prefix found in older MTK trace formats
    private const val CAP = """\[CAP\]\s*"""
    private const val MAIN_CAP = """(?:\[MAIN\])?\[CAP\]\s*"""

    // Regex for NR DL FSpCC definitions
    private val reNrDlFspcc =
        Regex(
            MAIN_CAP +
                """NR DL FSpCC\[(\d+)],\s*""" +
                """scs\[(\w+)],\s*bw\[(\w+)],\s*bw90m\[(\w+)],\s*""" +
                """mimo\[(\w+)],\s*modulation\[(\w+)]"""
        )

    // Regex for NR UL FSpCC definitions
    private val reNrUlFspcc =
        Regex(
            MAIN_CAP +
                """NR UL FSpCC\[(\d+)],\s*""" +
                """scs\[(\w+)],\s*bw\[(\w+)],\s*bw90m\[(\w+)],\s*""" +
                """cb_mimo\[(\w+)],.*?modulation\[(\w+)]"""
        )

    // Regex for EUTRA DL FSpCC definitions
    private val reEutraDlFspcc = Regex(MAIN_CAP + """EUTRA DL FSpCC\[(\d+)],.*?mimo\[(\w+)]""")

    // Regex for EUTRA UL FSpCC definitions (may include 256qam field)
    private val reEutraUlFspcc =
        Regex(MAIN_CAP + """EUTRA UL FSpCC\[(\d+)],\s*mimo\[(\w+)](?:,\s*256qam\[(\w+)])?""")

    // Regex for NR DL Feature Set -> FSpCC ID mappings
    // Old format has 2 brackets, new format has 8 brackets
    private val reNrDlFs =
        Regex(
            MAIN_CAP +
                """NR DL FS\[(\d+)],\s*FSDLpCC ID""" +
                """\[([^\]]*)]""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?"""
        )

    // Regex for NR UL Feature Set -> FSpCC ID mappings
    private val reNrUlFs =
        Regex(
            MAIN_CAP +
                """NR UL FS\[(\d+)],\s*FSULpCC ID""" +
                """\[([^\]]*)]""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?"""
        )

    // Regex for EUTRA DL Feature Set -> FSpCC ID mappings
    private val reEutraDlFs =
        Regex(
            MAIN_CAP +
                """EUTRA DL FS\[(\d+)],\s*FSDLpCC ID""" +
                """\[([^\]]*)]""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?"""
        )

    // Regex for EUTRA UL Feature Set -> FSpCC ID mappings
    private val reEutraUlFs =
        Regex(
            MAIN_CAP +
                """EUTRA UL FS\[(\d+)],\s*FSULpCC ID""" +
                """\[([^\]]*)]""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?""" +
                """(?:\[([^\]]*)])?"""
        )

    // Regex for FSC definitions
    // Old format: [MAIN][CAP] FSC[1], band num[1], D/U[...][...]
    // New format: [CAP] FSC[1], D/U[...][...]
    private val reFsc =
        Regex(MAIN_CAP + """FSC\[(\d+)],\s*(?:band num\[\d+],\s*)?D/U((?:\[[^\]]+/[^\]]+])+)""")
    private val reDuPair = Regex("""\[([^/]+)/([^\]]+)]""")

    // Regex for combo lines - supports both old and new trace formats:
    // New: [CAP]CA idx [0] NL1 bc, num[1] DL: ... UL: ... FSC[1], BC info[...]
    // Old: [MAIN][CAP] CA idx [1], NL1 CA band comb, band num[1], DL: ..., UL: ..., FSC[2]
    private val reComboNew =
        Regex(
            CAP +
                """CA idx \[(\d+)] NL1 bc, num\[(\d+)]\s+""" +
                """DL:\s+([\w_]+)\s+UL:\s+([\w_]+)\s+FSC\[(\d+)]"""
        )
    private val reComboOld =
        Regex(
            MAIN_CAP +
                """CA idx \[(\d+)],\s*NL1 CA band comb,\s*band num\[(\d+)],\s*""" +
                """DL:\s+([\w_]+),\s*UL:\s+([\w_]+),\s*FSC\[(\d+)]"""
        )

    // Regex for individual band component in DL/UL strings
    private val reBandComponent = Regex("""(B|N)(\d+)([A-Z])""")

    // Regex for FS reference strings like 'N7', 'E1', '_0'
    private val reFsRef = Regex("""([NE])(\d+)""")

    override fun parse(input: InputSource): Capabilities {
        val capabilities = Capabilities()
        val lines = input.readLines()

        val parsed = parseTraceLog(lines)
        val combos = buildCombos(parsed)

        capabilities.enDcCombos = combos.filterIsInstance<ComboEnDc>()
        capabilities.nrCombos = combos.filterIsInstance<ComboNr>()
        capabilities.nrDcCombos = combos.filterIsInstance<ComboNrDc>()

        return capabilities
    }

    /**
     * Data class holding a parsed FSC entry with variants.
     *
     * Some devices emit multiple FSC lines with the same FSC number but different FS references.
     * Each variant is a list of (dlFsRef, ulFsRef) per component.
     */
    private data class FscEntry(val variants: List<List<Pair<String, String>>>)

    /** Container for all parsed trace data. */
    private data class ParsedTrace(
        val nrDlFspcc: Map<Int, FeaturePerCCNr>,
        val nrUlFspcc: Map<Int, FeaturePerCCNr>,
        val eutraDlFspcc: Map<Int, FeaturePerCCLte>,
        val eutraUlFspcc: Map<Int, FeaturePerCCLte>,
        val nrDlFs: Map<Int, List<Int>>,
        val nrUlFs: Map<Int, List<Int>>,
        val eutraDlFs: Map<Int, List<Int>>,
        val eutraUlFs: Map<Int, List<Int>>,
        val fscDefs: Map<Int, FscEntry>,
        val combos: List<ICombo>,
    )

    /** Parse all capability structures from the trace log lines. */
    private fun parseTraceLog(lines: List<String>): ParsedTrace {
        val nrDlFspcc = mutableMapOf<Int, FeaturePerCCNr>()
        val nrUlFspcc = mutableMapOf<Int, FeaturePerCCNr>()
        val eutraDlFspcc = mutableMapOf<Int, FeaturePerCCLte>()
        val eutraUlFspcc = mutableMapOf<Int, FeaturePerCCLte>()
        val nrDlFs = mutableMapOf<Int, List<Int>>()
        val nrUlFs = mutableMapOf<Int, List<Int>>()
        val eutraDlFs = mutableMapOf<Int, List<Int>>()
        val eutraUlFs = mutableMapOf<Int, List<Int>>()
        val fscDefs = mutableMapOf<Int, FscEntry>()
        val combos = mutableListOf<ICombo>()

        for (line in lines) {
            val tag = extractTag(line)

            when (tag) {
                "CA idx" -> parseComboLine(line, combos)
                "NR DL FSpCC" -> parseNrDlFspccLine(line, nrDlFspcc)
                "NR UL FSpCC" -> parseNrUlFspccLine(line, nrUlFspcc)
                "EUTRA DL FSpCC" -> parseEutraDlFspccLine(line, eutraDlFspcc)
                "EUTRA UL FSpCC" -> parseEutraUlFspccLine(line, eutraUlFspcc)
                "NR DL FS" -> parseNrDlFsLine(line, nrDlFs)
                "NR UL FS" -> parseNrUlFsLine(line, nrUlFs)
                "EUTRA DL FS" -> parseEutraDlFsLine(line, eutraDlFs)
                "EUTRA UL FS" -> parseEutraUlFsLine(line, eutraUlFs)
                "FSC" -> parseFscLine(line, fscDefs)
                else -> {
                    // do nothing
                }
            }
        }

        return ParsedTrace(
            nrDlFspcc,
            nrUlFspcc,
            eutraDlFspcc,
            eutraUlFspcc,
            nrDlFs,
            nrUlFs,
            eutraDlFs,
            eutraUlFs,
            fscDefs,
            combos,
        )
    }

    /**
     * Extract the tag, that is reported in the line after the &#91;CAP&#93; header and before the
     * index number. If the line doesn't contain the word &#91;CAP&#93; it returns an empty string.
     *
     * Example: "211987, 156011, 9956508, 16:33:37:615 2026/03/14, 16:33:37:716544 2026/03/14,
     * MOD_NRRC_MAIN, MOD_NRRC_MAIN_BASELINE_TRACE_INFO_H, &#91;MAIN&#93; &#91;CAP&#93; CA idx [17]"
     * -> tag CA idx
     */
    private fun extractTag(line: String): String {
        // Extract text After [MAIN][CAP]/[CAP]
        var tag = line.substringAfter("[CAP]", "")

        // remove [index number] and text after index number
        tag = tag.split("[").first()

        // trim
        tag = tag.trim()

        return tag
    }

    /** Parse a NR DL FSpCC definition line. */
    private fun parseNrDlFspccLine(line: String, map: MutableMap<Int, FeaturePerCCNr>): Unit? {
        val m = reNrDlFspcc.find(line) ?: return null
        val idx = m.groupValues[1].toInt()
        val scs = parseMtkScs(m.groupValues[2])
        val bwRaw = parseMtkBw(m.groupValues[3])
        val bw90 = m.groupValues[4] == "NL1_CAP_SUPPORT"
        val bw = if (bw90 && bwRaw == 80) 90 else bwRaw
        map[idx] =
            FeaturePerCCNr(
                type = LinkDirection.DOWNLINK,
                mimo = parseMtkDlMimo(m.groupValues[5]).toMimo(),
                qam = parseMtkModulation(m.groupValues[6]),
                bw = bw,
                scs = scs,
                channelBW90mhz = bw90,
            )
        return Unit
    }

    /** Parse a NR UL FSpCC definition line. */
    private fun parseNrUlFspccLine(line: String, map: MutableMap<Int, FeaturePerCCNr>): Unit? {
        val m = reNrUlFspcc.find(line) ?: return null
        val idx = m.groupValues[1].toInt()
        val scs = parseMtkScs(m.groupValues[2])
        val bwRaw = parseMtkBw(m.groupValues[3])
        val bw90 = m.groupValues[4] == "NL1_CAP_SUPPORT"
        val bw = if (bw90 && bwRaw == 80) 90 else bwRaw
        map[idx] =
            FeaturePerCCNr(
                type = LinkDirection.UPLINK,
                mimo = parseMtkUlMimo(m.groupValues[5]).toMimo(),
                qam = parseMtkModulation(m.groupValues[6]),
                bw = bw,
                scs = scs,
                channelBW90mhz = bw90,
            )
        return Unit
    }

    /** Parse a EUTRA DL FSpCC definition line. */
    private fun parseEutraDlFspccLine(line: String, map: MutableMap<Int, FeaturePerCCLte>): Unit? {
        val m = reEutraDlFspcc.find(line) ?: return null
        val idx = m.groupValues[1].toInt()
        val mimoStr = m.groupValues[2]
        val mimo =
            when {
                "FOUR_LAYER" in mimoStr -> 4
                "TWO_LAYER" in mimoStr -> 2
                else -> 0
            }
        map[idx] = FeaturePerCCLte(type = LinkDirection.DOWNLINK, mimo = mimo.toMimo())
        return Unit
    }

    /** Parse a EUTRA UL FSpCC definition line. */
    private fun parseEutraUlFspccLine(line: String, map: MutableMap<Int, FeaturePerCCLte>): Unit? {
        val m = reEutraUlFspcc.find(line) ?: return null
        val idx = m.groupValues[1].toInt()
        val mimoStr = m.groupValues[2]
        val mimo =
            when {
                "TWO_LAYER" in mimoStr -> 2
                "ONE_LAYER" in mimoStr -> 1
                else -> 0
            }
        map[idx] = FeaturePerCCLte(type = LinkDirection.UPLINK, mimo = mimo.toMimo())
        return Unit
    }

    /** Parse a NR DL FS -> FSpCC ID mapping line. */
    private fun parseNrDlFsLine(line: String, map: MutableMap<Int, List<Int>>): Unit? {
        val m = reNrDlFs.find(line) ?: return null
        val fsNum = m.groupValues[1].toInt()
        val ids = extractFspccIds(m, startGroup = 2, endGroup = 9)
        map[fsNum] = ids
        return Unit
    }

    /** Parse a NR UL FS -> FSpCC ID mapping line. */
    private fun parseNrUlFsLine(line: String, map: MutableMap<Int, List<Int>>): Unit? {
        val m = reNrUlFs.find(line) ?: return null
        val fsNum = m.groupValues[1].toInt()
        val ids = extractFspccIds(m, startGroup = 2, endGroup = 5)
        map[fsNum] = ids
        return Unit
    }

    /** Parse an EUTRA DL FS -> FSpCC ID mapping line. */
    private fun parseEutraDlFsLine(line: String, map: MutableMap<Int, List<Int>>): Unit? {
        val m = reEutraDlFs.find(line) ?: return null
        val fsNum = m.groupValues[1].toInt()
        val ids = extractFspccIds(m, startGroup = 2, endGroup = 6)
        map[fsNum] = ids
        return Unit
    }

    /** Parse an EUTRA UL FS -> FSpCC ID mapping line. */
    private fun parseEutraUlFsLine(line: String, map: MutableMap<Int, List<Int>>): Unit? {
        val m = reEutraUlFs.find(line) ?: return null
        val fsNum = m.groupValues[1].toInt()
        val ids = extractFspccIds(m, startGroup = 2, endGroup = 6)
        map[fsNum] = ids
        return Unit
    }

    /** Parse an FSC definition line. Collects all variants for each FSC number. */
    private fun parseFscLine(line: String, map: MutableMap<Int, FscEntry>): Unit? {
        val m = reFsc.find(line) ?: return null
        val fscNum = m.groupValues[1].toInt()

        val pairsStr = m.groupValues[2]
        val pairs = mutableListOf<Pair<String, String>>()
        for (pm in reDuPair.findAll(pairsStr)) {
            val dl = pm.groupValues[1].trim()
            val ul = pm.groupValues[2].trim()
            if (dl == "_0" && ul == "_0") continue // Skip empty slots
            pairs.add(Pair(dl, ul))
        }

        val existing = map[fscNum]
        if (existing != null) {
            // Add as another variant
            map[fscNum] = FscEntry(existing.variants + listOf(pairs))
        } else {
            map[fscNum] = FscEntry(listOf(pairs))
        }
        return Unit
    }

    /** Parse a combo definition line. Tries new format first, then old format. */
    private fun parseComboLine(line: String, list: MutableList<ICombo>): Unit? {
        val m = reComboNew.find(line) ?: reComboOld.find(line) ?: return null
        val dlStr = m.groupValues[3]
        val ulStr = m.groupValues[4]
        val fscNum = m.groupValues[5].toInt()

        val components = parseComboComponents(dlStr, ulStr)
        if (components.isNotEmpty()) {
            val lteComponents = components.filterIsInstance<ComponentLte>()
            val nrComponents = components.filterIsInstance<ComponentNr>()
            val combo =
                if (lteComponents.isEmpty()) {
                    ComboNr(nrComponents, featureSet = fscNum)
                } else {
                    ComboEnDc(lteComponents, nrComponents, featureSet = fscNum)
                }
            list.add(combo)
        }
        return Unit
    }

    /**
     * Parse DL and UL component strings into a merged list of components.
     *
     * DL and UL strings are underscore-separated with positional matching. For example: DL:
     * B1A_B3A_N78A__0_... UL: B1A__0__N78A__0_...
     */
    private fun parseComboComponents(dlStr: String, ulStr: String): List<IComponent> {
        val dlParts = dlStr.split("_")
        val ulParts = ulStr.split("_")

        // Parse DL components with their positions
        data class DlEntry(val isNr: Boolean, val band: Int, val bwClass: Char, val pos: Int)

        val dlEntries = mutableListOf<DlEntry>()
        for ((i, part) in dlParts.withIndex()) {
            if (part.isEmpty() || part == "0") continue
            val bm = reBandComponent.matchEntire(part) ?: continue
            dlEntries.add(
                DlEntry(
                    isNr = bm.groupValues[1] == "N",
                    band = bm.groupValues[2].toInt(),
                    bwClass = bm.groupValues[3][0],
                    pos = i,
                )
            )
        }

        // Parse UL components with their positions
        data class UlEntry(val isNr: Boolean, val band: Int, val bwClass: Char, val pos: Int)

        val ulEntries = mutableListOf<UlEntry>()
        for ((i, part) in ulParts.withIndex()) {
            if (part.isEmpty() || part == "0") continue
            val bm = reBandComponent.matchEntire(part) ?: continue
            ulEntries.add(
                UlEntry(
                    isNr = bm.groupValues[1] == "N",
                    band = bm.groupValues[2].toInt(),
                    bwClass = bm.groupValues[3][0],
                    pos = i,
                )
            )
        }

        // Merge: for each DL component, find matching UL by position first, then by band
        val result = mutableListOf<IComponent>()
        val usedUl = mutableSetOf<Int>() // indices into ulEntries that have been matched

        for (dl in dlEntries) {
            // Try positional match first
            var ulBwClass: Char? = null
            val posMatch =
                ulEntries.indexOfFirst {
                    it.pos == dl.pos && it !in usedUl.map { idx -> ulEntries[idx] }
                }
            if (
                posMatch >= 0 &&
                    ulEntries[posMatch].band == dl.band &&
                    ulEntries[posMatch].isNr == dl.isNr
            ) {
                ulBwClass = ulEntries[posMatch].bwClass
                usedUl.add(posMatch)
            } else {
                // Try matching by band (first unmatched UL with same band and type)
                val bandMatch =
                    ulEntries.withIndex().firstOrNull { (idx, ul) ->
                        idx !in usedUl && ul.band == dl.band && ul.isNr == dl.isNr
                    }
                if (bandMatch != null) {
                    ulBwClass = bandMatch.value.bwClass
                    usedUl.add(bandMatch.index)
                }
            }

            val classDlBw = BwClass.valueOf(dl.bwClass.toString())
            val classUlBw =
                if (ulBwClass != null) BwClass.valueOf(ulBwClass.toString()) else BwClass.NONE

            if (dl.isNr) {
                result.add(ComponentNr(dl.band, classDlBw, classUlBw))
            } else {
                val mimoUl = if (classUlBw != BwClass.NONE) 1.toMimo() else EmptyMimo
                result.add(ComponentLte(dl.band, classDlBw, classUlBw, mimoUL = mimoUl))
            }
        }

        return result
    }

    /** Extract FSpCC IDs from a regex match, filtering out zeros. */
    private fun extractFspccIds(match: MatchResult, startGroup: Int, endGroup: Int): List<Int> {
        val ids = mutableListOf<Int>()
        for (i in startGroup..endGroup) {
            val g = match.groupValues.getOrNull(i)
            if (!g.isNullOrEmpty()) {
                val value = g.toIntOrNull() ?: continue
                if (value > 0) ids.add(value)
            }
        }
        return ids
    }

    /** Convert MTK SCS enum string to SCS value in kHz. */
    private fun parseMtkScs(scsStr: String): Int {
        return when (scsStr) {
            "NL1_CAP_SCS_15KHZ" -> 15
            "NL1_CAP_SCS_30KHZ" -> 30
            "NL1_CAP_SCS_60KHZ" -> 60
            "NL1_CAP_SCS_120KHZ" -> 120
            else -> 15
        }
    }

    /** Extract bandwidth integer from strings like 'NL1_CAP_BW50'. */
    private fun parseMtkBw(bwStr: String): Int {
        val m = Regex("""NL1_CAP_BW(\d+)""").find(bwStr)
        return m?.groupValues?.get(1)?.toInt() ?: 0
    }

    /** Convert MTK DL MIMO enum string to layer count. */
    private fun parseMtkDlMimo(mimoStr: String): Int {
        return when (mimoStr) {
            "NL1_CAP_DL_TWO_LAYER" -> 2
            "NL1_CAP_DL_FOUR_LAYER" -> 4
            "NL1_CAP_DL_EIGHT_LAYER" -> 8
            else -> 0
        }
    }

    /** Convert MTK UL MIMO enum string to layer count. */
    private fun parseMtkUlMimo(mimoStr: String): Int {
        return when (mimoStr) {
            "NL1_CAP_UL_ONE_LAYER" -> 1
            "NL1_CAP_UL_TWO_LAYER" -> 2
            "NL1_CAP_UL_FOUR_LAYER" -> 4
            else -> 0
        }
    }

    /** Convert MTK modulation enum string to ModulationOrder. */
    private fun parseMtkModulation(modStr: String): ModulationOrder {
        return when (modStr) {
            "NL1_CAP_64QAM" -> ModulationOrder.QAM64
            "NL1_CAP_256QAM" -> ModulationOrder.QAM256
            "NL1_CAP_1024QAM" -> ModulationOrder.QAM1024
            else -> ModulationOrder.NONE
        }
    }

    /** Parse an FS reference string like 'N7', 'E1', '_0' into type and number. */
    private fun parseFsRef(fsStr: String): Pair<Char?, Int> {
        if (fsStr == "_0" || fsStr == "0" || fsStr == "N0") return Pair(null, 0)
        val m = reFsRef.matchEntire(fsStr) ?: return Pair(null, 0)
        return Pair(m.groupValues[1][0], m.groupValues[2].toInt())
    }

    /** Build Capabilities combos from parsed trace data. */
    private fun buildCombos(parsed: ParsedTrace): List<ICombo> {
        val combos = mutableListWithCapacity<ICombo>(parsed.combos.size)

        // Build EUTRA Feature Sets: FS number -> FeatureSet (list of per-CC features)
        val eutraDlFeatureSets =
            buildEutraFeatureSets(parsed.eutraDlFs, parsed.eutraDlFspcc, LinkDirection.DOWNLINK)
        val eutraUlFeatureSets =
            buildEutraFeatureSets(parsed.eutraUlFs, parsed.eutraUlFspcc, LinkDirection.UPLINK)

        for (combo in parsed.combos) {
            val fscEntry = parsed.fscDefs[combo.featureSet] ?: continue
            val components = combo.masterComponents + combo.secondaryComponents

            // Emit one combo per FSC variant (all variants are listed)
            for (variant in fscEntry.variants) {
                // FSC pairs might not exactly match component count; align by min
                val count = minOf(variant.size, components.size)
                if (count == 0) continue

                val nrComponents = mutableListOf<ComponentNr>()
                val lteComponents = mutableListOf<ComponentLte>()

                for (i in 0 until count) {
                    val comp = components[i]
                    val (dlFsStr, ulFsStr) = variant[i]
                    val (dlFsType, dlFsNum) = parseFsRef(dlFsStr)
                    val (ulFsType, ulFsNum) = parseFsRef(ulFsStr)

                    if (comp is ComponentNr) {
                        val nrComp =
                            buildNrComponent(comp, dlFsType, dlFsNum, ulFsType, ulFsNum, parsed)
                        nrComponents.add(nrComp)
                    } else if (comp is ComponentLte) {
                        val lteComp =
                            buildLteComponent(
                                comp,
                                dlFsType,
                                dlFsNum,
                                ulFsType,
                                ulFsNum,
                                eutraDlFeatureSets,
                                eutraUlFeatureSets,
                            )
                        lteComponents.add(lteComp)
                    }
                }

                val icombo = assembleCombo(lteComponents, nrComponents)
                if (icombo != null) combos.add(icombo)
            }
        }

        return combos
    }

    /**
     * Build EUTRA Feature Sets: map from FS number to FeatureSet, resolving FSpCC IDs to actual
     * per-CC features.
     */
    private fun buildEutraFeatureSets(
        fsMap: Map<Int, List<Int>>,
        fspccFeatures: Map<Int, FeaturePerCCLte>,
        direction: LinkDirection,
    ): Map<Int, FeatureSet> {
        return fsMap.mapValues { (_, fspccIds) ->
            val perCCList = fspccIds.mapNotNull { fspccFeatures[it] }
            FeatureSet(perCCList, direction)
        }
    }

    /** Build an NR ComponentNr from parsed component data, resolving features via FSC. */
    private fun buildNrComponent(
        comp: ComponentNr,
        dlFsType: Char?,
        dlFsNum: Int,
        ulFsType: Char?,
        ulFsNum: Int,
        parsed: ParsedTrace,
    ): ComponentNr {
        val baseComponent = comp.copy()

        // Resolve DL per-CC features: FS -> FSpCC IDs -> FeaturePerCCNr
        val dlFeature: List<FeaturePerCCNr> =
            if (dlFsType == 'N' && dlFsNum > 0) {
                val fspccIds = parsed.nrDlFs[dlFsNum] ?: emptyList()
                fspccIds.mapNotNull { parsed.nrDlFspcc[it] }
            } else {
                emptyList()
            }

        // Resolve UL per-CC features
        val ulFeature: List<FeaturePerCCNr> =
            if (ulFsType == 'N' && ulFsNum > 0) {
                val fspccIds = parsed.nrUlFs[ulFsNum] ?: emptyList()
                fspccIds.mapNotNull { parsed.nrUlFspcc[it] }
            } else {
                emptyList()
            }

        return mergeComponentAndFeaturePerCC(baseComponent, dlFeature, ulFeature, null)
            as ComponentNr
    }

    /** Build an LTE ComponentLte from parsed component data, resolving features via FSC. */
    private fun buildLteComponent(
        comp: ComponentLte,
        dlFsType: Char?,
        dlFsNum: Int,
        ulFsType: Char?,
        ulFsNum: Int,
        eutraDlFeatureSets: Map<Int, FeatureSet>,
        eutraUlFeatureSets: Map<Int, FeatureSet>,
    ): ComponentLte {
        val baseComponent = comp.copy()

        // Resolve DL features from EUTRA FS
        val dlFeature: List<IFeaturePerCC>? =
            if (dlFsType == 'E' && dlFsNum > 0) {
                eutraDlFeatureSets[dlFsNum]?.featureSetsPerCC
            } else {
                null
            }

        // Resolve UL features from EUTRA FS
        val ulFeature: List<IFeaturePerCC>? =
            if (ulFsType == 'E' && ulFsNum > 0) {
                eutraUlFeatureSets[ulFsNum]?.featureSetsPerCC
            } else {
                null
            }

        return mergeComponentAndFeaturePerCC(baseComponent, dlFeature, ulFeature, null)
            as ComponentLte
    }

    /** Assemble the final combo (NR-CA, NR-DC, or EN-DC) from component lists. */
    private fun assembleCombo(
        lteComponents: List<ComponentLte>,
        nrComponents: List<ComponentNr>,
    ): ICombo? {
        if (nrComponents.isEmpty() && lteComponents.isEmpty()) return null

        val sortedNr = nrComponents.sortedDescending()
        val sortedLte = lteComponents.sortedDescending()

        return if (sortedLte.isEmpty()) {
            // NR-CA or NR-DC
            if (sortedNr.none { it.isFR2 } || sortedNr.none { !it.isFR2 }) {
                ComboNr(sortedNr)
            } else {
                // Both FR1 and FR2 present -> NR-DC
                val (fr2, fr1) = sortedNr.partition { it.isFR2 }
                ComboNrDc(fr1, fr2)
            }
        } else {
            // EN-DC
            ComboEnDc(sortedLte, sortedNr)
        }
    }
}
