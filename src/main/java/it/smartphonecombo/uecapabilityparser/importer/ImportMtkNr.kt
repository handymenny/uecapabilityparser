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

    /** Data class holding a parsed NR DL/UL per-CC feature. */
    private data class NrFspcc(
        val scs: Int,
        val bw: Int,
        val bw90: Boolean,
        val mimo: Int,
        val mod: ModulationOrder,
    )

    /** Data class holding a parsed EUTRA DL/UL per-CC feature. */
    private data class EutraFspcc(val mimo: Int)

    /**
     * Data class holding a parsed FSC entry with SCS variants.
     *
     * Some devices emit multiple FSC lines with the same FSC number but different FS references,
     * corresponding to different sub-carrier spacings (SCS15 for FDD bands, SCS30 for TDD bands).
     * Each variant is a list of (dlFsRef, ulFsRef) per component.
     */
    private data class FscEntry(val variants: List<List<Pair<String, String>>>)

    /** Data class holding a parsed combo. */
    private data class MtkCombo(val components: List<MtkComponent>, val fscNum: Int)

    /** Data class holding a parsed component from combo DL/UL strings. */
    private data class MtkComponent(
        val isNr: Boolean,
        val band: Int,
        val bwClassDl: Char,
        val bwClassUl: Char?, // null if no UL
    )

    /** Container for all parsed trace data. */
    private data class ParsedTrace(
        val nrDlFspcc: Map<Int, NrFspcc>,
        val nrUlFspcc: Map<Int, NrFspcc>,
        val eutraDlFspcc: Map<Int, EutraFspcc>,
        val eutraUlFspcc: Map<Int, EutraFspcc>,
        val nrDlFs: Map<Int, List<Int>>,
        val nrUlFs: Map<Int, List<Int>>,
        val eutraDlFs: Map<Int, List<Int>>,
        val eutraUlFs: Map<Int, List<Int>>,
        val fscDefs: Map<Int, FscEntry>,
        val combos: List<MtkCombo>,
    )

    /** Parse all capability structures from the trace log lines. */
    private fun parseTraceLog(lines: List<String>): ParsedTrace {
        val nrDlFspcc = mutableMapOf<Int, NrFspcc>()
        val nrUlFspcc = mutableMapOf<Int, NrFspcc>()
        val eutraDlFspcc = mutableMapOf<Int, EutraFspcc>()
        val eutraUlFspcc = mutableMapOf<Int, EutraFspcc>()
        val nrDlFs = mutableMapOf<Int, List<Int>>()
        val nrUlFs = mutableMapOf<Int, List<Int>>()
        val eutraDlFs = mutableMapOf<Int, List<Int>>()
        val eutraUlFs = mutableMapOf<Int, List<Int>>()
        val fscDefs = mutableMapOf<Int, FscEntry>()
        val combos = mutableListOf<MtkCombo>()

        for (line in lines) {
            // Try each pattern - at most one will match per line
            parseNrDlFspccLine(line, nrDlFspcc)
                ?: parseNrUlFspccLine(line, nrUlFspcc)
                ?: parseEutraDlFspccLine(line, eutraDlFspcc)
                ?: parseEutraUlFspccLine(line, eutraUlFspcc)
                ?: parseNrDlFsLine(line, nrDlFs)
                ?: parseNrUlFsLine(line, nrUlFs)
                ?: parseEutraDlFsLine(line, eutraDlFs)
                ?: parseEutraUlFsLine(line, eutraUlFs)
                ?: parseFscLine(line, fscDefs)
                ?: parseComboLine(line, combos)
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

    /** Parse a NR DL FSpCC definition line. */
    private fun parseNrDlFspccLine(line: String, map: MutableMap<Int, NrFspcc>): Unit? {
        val m = reNrDlFspcc.find(line) ?: return null
        val idx = m.groupValues[1].toInt()
        map[idx] =
            NrFspcc(
                scs = parseMtkScs(m.groupValues[2]),
                bw = parseMtkBw(m.groupValues[3]),
                bw90 = m.groupValues[4] == "NL1_CAP_SUPPORT",
                mimo = parseMtkDlMimo(m.groupValues[5]),
                mod = parseMtkModulation(m.groupValues[6]),
            )
        return Unit
    }

    /** Parse a NR UL FSpCC definition line. */
    private fun parseNrUlFspccLine(line: String, map: MutableMap<Int, NrFspcc>): Unit? {
        val m = reNrUlFspcc.find(line) ?: return null
        val idx = m.groupValues[1].toInt()
        map[idx] =
            NrFspcc(
                scs = parseMtkScs(m.groupValues[2]),
                bw = parseMtkBw(m.groupValues[3]),
                bw90 = m.groupValues[4] == "NL1_CAP_SUPPORT",
                mimo = parseMtkUlMimo(m.groupValues[5]),
                mod = parseMtkModulation(m.groupValues[6]),
            )
        return Unit
    }

    /** Parse a EUTRA DL FSpCC definition line. */
    private fun parseEutraDlFspccLine(line: String, map: MutableMap<Int, EutraFspcc>): Unit? {
        val m = reEutraDlFspcc.find(line) ?: return null
        val idx = m.groupValues[1].toInt()
        val mimoStr = m.groupValues[2]
        val mimo =
            when {
                "FOUR_LAYER" in mimoStr -> 4
                "TWO_LAYER" in mimoStr -> 2
                else -> 0
            }
        map[idx] = EutraFspcc(mimo)
        return Unit
    }

    /** Parse a EUTRA UL FSpCC definition line. */
    private fun parseEutraUlFspccLine(line: String, map: MutableMap<Int, EutraFspcc>): Unit? {
        val m = reEutraUlFspcc.find(line) ?: return null
        val idx = m.groupValues[1].toInt()
        val mimoStr = m.groupValues[2]
        val mimo =
            when {
                "TWO_LAYER" in mimoStr -> 2
                "ONE_LAYER" in mimoStr -> 1
                else -> 0
            }
        map[idx] = EutraFspcc(mimo)
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

    /** Parse an FSC definition line. Collects all SCS variants for each FSC number. */
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
            // Add as another SCS variant
            map[fscNum] = FscEntry(existing.variants + listOf(pairs))
        } else {
            map[fscNum] = FscEntry(listOf(pairs))
        }
        return Unit
    }

    /** Parse a combo definition line. Tries new format first, then old format. */
    private fun parseComboLine(line: String, list: MutableList<MtkCombo>): Unit? {
        val m = reComboNew.find(line) ?: reComboOld.find(line) ?: return null
        val dlStr = m.groupValues[3]
        val ulStr = m.groupValues[4]
        val fscNum = m.groupValues[5].toInt()

        val components = parseComboComponents(dlStr, ulStr)
        if (components.isNotEmpty()) {
            list.add(MtkCombo(components, fscNum))
        }
        return Unit
    }

    /**
     * Parse DL and UL component strings into a merged list of components.
     *
     * DL and UL strings are underscore-separated with positional matching. For example: DL:
     * B1A_B3A_N78A__0_... UL: B1A__0__N78A__0_...
     */
    private fun parseComboComponents(dlStr: String, ulStr: String): List<MtkComponent> {
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
        val result = mutableListOf<MtkComponent>()
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

            result.add(MtkComponent(dl.isNr, dl.band, dl.bwClass, ulBwClass))
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

    /** NR FDD band numbers — use SCS 15 kHz features for these. */
    private val nrFddBands =
        setOf(
            1,
            2,
            3,
            5,
            7,
            8,
            12,
            13,
            14,
            18,
            20,
            24,
            25,
            26,
            28,
            30,
            31,
            65,
            66,
            68,
            70,
            71,
            72,
            74,
            75,
            85,
            87,
            88,
            91,
            92,
            93,
            94,
            100,
            105,
            106,
            109,
            110,
        )

    /** NR TDD band numbers — use SCS 30 kHz features for these. */
    private val nrTddBands =
        setOf(34, 38, 39, 40, 41, 46, 47, 48, 50, 53, 77, 78, 79, 90, 96, 101, 102, 104)

    /** Return the preferred SCS (15 or 30) for the given NR band number. */
    private fun preferredScsForBand(band: Int): Int {
        return if (band in nrTddBands) 30 else 15
    }

    /**
     * Resolve the SCS of a specific NR DL FS reference.
     *
     * Returns 0 if the SCS cannot be determined.
     */
    private fun resolveNrFsScs(dlFsStr: String, parsed: ParsedTrace): Int {
        val (fsType, fsNum) = parseFsRef(dlFsStr)
        if (fsType != 'N' || fsNum <= 0) return 0
        val fspccIds = parsed.nrDlFs[fsNum] ?: return 0
        val firstFspccId = fspccIds.firstOrNull() ?: return 0
        val fspcc = parsed.nrDlFspcc[firstFspccId] ?: return 0
        return fspcc.scs
    }

    /**
     * Build the best FSC pair list for the given combo components by selecting, for each component
     * position, the FS reference pair whose SCS matches that band's duplex mode (FDD → SCS 15 kHz,
     * TDD → SCS 30 kHz).
     *
     * FSC variants are a cartesian product of per-component SCS options. For example FSC[12] with
     * N3(FDD)+N78(TDD) may have 4 variants: (SCS15,SCS15), (SCS15,SCS30), (SCS30,SCS15),
     * (SCS30,SCS30). The correct pick is (SCS15, SCS30) → N3 at SCS15, N78 at SCS30.
     *
     * Falls back to the first variant if only one variant exists.
     */
    private fun selectFscPairs(
        fscEntry: FscEntry,
        components: List<MtkComponent>,
        parsed: ParsedTrace,
    ): List<Pair<String, String>> {
        if (fscEntry.variants.size <= 1) return fscEntry.variants.first()

        val pairCount = fscEntry.variants.first().size
        val result = mutableListWithCapacity<Pair<String, String>>(pairCount)

        for (i in 0 until pairCount) {
            // Collect all unique (dl, ul) candidates at position i across all variants
            val candidates = fscEntry.variants.map { it[i] }.distinct()

            if (candidates.size <= 1 || i >= components.size) {
                result.add(candidates.first())
                continue
            }

            val comp = components[i]
            if (!comp.isNr) {
                // EUTRA components don't vary by SCS
                result.add(candidates.first())
                continue
            }

            // Pick the candidate whose DL FS resolves to the preferred SCS for this band
            val preferredScs = preferredScsForBand(comp.band)
            val best =
                candidates.firstOrNull { resolveNrFsScs(it.first, parsed) == preferredScs }
                    ?: candidates.first()
            result.add(best)
        }

        return result
    }

    /** Build Capabilities combos from parsed trace data. */
    private fun buildCombos(parsed: ParsedTrace): List<ICombo> {
        val combos = mutableListWithCapacity<ICombo>(parsed.combos.size)

        // Build NR per-CC feature lists (indexed directly by FSpCC ID)
        val nrFeaturesPerCCDl = buildNrFeaturesPerCC(parsed.nrDlFspcc, LinkDirection.DOWNLINK)
        val nrFeaturesPerCCUl = buildNrFeaturesPerCC(parsed.nrUlFspcc, LinkDirection.UPLINK)

        // Build EUTRA per-CC feature lists (indexed directly by FSpCC ID)
        val eutraFeaturesPerCCDl =
            buildEutraFeaturesPerCC(parsed.eutraDlFspcc, LinkDirection.DOWNLINK)
        val eutraFeaturesPerCCUl =
            buildEutraFeaturesPerCC(parsed.eutraUlFspcc, LinkDirection.UPLINK)

        // Build EUTRA Feature Sets: FS number -> FeatureSet (list of per-CC features)
        val eutraDlFeatureSets =
            buildEutraFeatureSets(parsed.eutraDlFs, eutraFeaturesPerCCDl, LinkDirection.DOWNLINK)
        val eutraUlFeatureSets =
            buildEutraFeatureSets(parsed.eutraUlFs, eutraFeaturesPerCCUl, LinkDirection.UPLINK)

        for (combo in parsed.combos) {
            val fscEntry = parsed.fscDefs[combo.fscNum] ?: continue
            val components = combo.components

            // Select the correct SCS-matched FS pairs for this combo's bands
            val fscPairs = selectFscPairs(fscEntry, components, parsed)

            // FSC pairs might not exactly match component count; align by min
            val count = minOf(fscPairs.size, components.size)
            if (count == 0) continue

            val nrComponents = mutableListOf<ComponentNr>()
            val lteComponents = mutableListOf<ComponentLte>()

            for (i in 0 until count) {
                val comp = components[i]
                val (dlFsStr, ulFsStr) = fscPairs[i]
                val (dlFsType, dlFsNum) = parseFsRef(dlFsStr)
                val (ulFsType, ulFsNum) = parseFsRef(ulFsStr)

                if (comp.isNr) {
                    val nrComp =
                        buildNrComponent(
                            comp,
                            dlFsType,
                            dlFsNum,
                            ulFsType,
                            ulFsNum,
                            parsed,
                            nrFeaturesPerCCDl,
                            nrFeaturesPerCCUl,
                        )
                    nrComponents.add(nrComp)
                } else {
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

        return combos
    }

    /** Build a map of FSpCC ID -> FeaturePerCCNr for NR features. */
    private fun buildNrFeaturesPerCC(
        fspccMap: Map<Int, NrFspcc>,
        direction: LinkDirection,
    ): Map<Int, FeaturePerCCNr> {
        return fspccMap.mapValues { (_, fspcc) ->
            val bw = if (fspcc.bw90 && fspcc.bw == 80) 90 else fspcc.bw
            FeaturePerCCNr(
                type = direction,
                mimo = fspcc.mimo.toMimo(),
                qam = fspcc.mod,
                bw = bw,
                scs = fspcc.scs,
                channelBW90mhz = fspcc.bw90,
            )
        }
    }

    /** Build a map of FSpCC ID -> FeaturePerCCLte for EUTRA features. */
    private fun buildEutraFeaturesPerCC(
        fspccMap: Map<Int, EutraFspcc>,
        direction: LinkDirection,
    ): Map<Int, FeaturePerCCLte> {
        return fspccMap.mapValues { (_, fspcc) ->
            FeaturePerCCLte(type = direction, mimo = fspcc.mimo.toMimo())
        }
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
        comp: MtkComponent,
        dlFsType: Char?,
        dlFsNum: Int,
        ulFsType: Char?,
        ulFsNum: Int,
        parsed: ParsedTrace,
        nrFeaturesPerCCDl: Map<Int, FeaturePerCCNr>,
        nrFeaturesPerCCUl: Map<Int, FeaturePerCCNr>,
    ): ComponentNr {
        val classDl = BwClass.valueOf(comp.bwClassDl.toString())
        val classUl =
            if (comp.bwClassUl != null) BwClass.valueOf(comp.bwClassUl.toString()) else BwClass.NONE
        val baseComponent = ComponentNr(comp.band, classDl, classUl)

        // Resolve DL per-CC features: FS -> FSpCC IDs -> FeaturePerCCNr
        val dlFeature: List<FeaturePerCCNr> =
            if (dlFsType == 'N' && dlFsNum > 0) {
                val fspccIds = parsed.nrDlFs[dlFsNum] ?: emptyList()
                fspccIds.mapNotNull { nrFeaturesPerCCDl[it] }
            } else {
                emptyList()
            }

        // Resolve UL per-CC features
        val ulFeature: List<FeaturePerCCNr> =
            if (ulFsType == 'N' && ulFsNum > 0) {
                val fspccIds = parsed.nrUlFs[ulFsNum] ?: emptyList()
                fspccIds.mapNotNull { nrFeaturesPerCCUl[it] }
            } else {
                emptyList()
            }

        return mergeComponentAndFeaturePerCC(baseComponent, dlFeature, ulFeature, null)
            as ComponentNr
    }

    /** Build an LTE ComponentLte from parsed component data, resolving features via FSC. */
    private fun buildLteComponent(
        comp: MtkComponent,
        dlFsType: Char?,
        dlFsNum: Int,
        ulFsType: Char?,
        ulFsNum: Int,
        eutraDlFeatureSets: Map<Int, FeatureSet>,
        eutraUlFeatureSets: Map<Int, FeatureSet>,
    ): ComponentLte {
        val classDl = BwClass.valueOf(comp.bwClassDl.toString())
        val classUl =
            if (comp.bwClassUl != null) BwClass.valueOf(comp.bwClassUl.toString()) else BwClass.NONE
        val mimoUl = if (classUl != BwClass.NONE) 1.toMimo() else EmptyMimo
        val baseComponent = ComponentLte(comp.band, classDl, classUl, mimoUL = mimoUl)

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
