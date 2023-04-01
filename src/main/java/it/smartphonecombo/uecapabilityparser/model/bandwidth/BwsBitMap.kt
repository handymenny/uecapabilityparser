package it.smartphonecombo.uecapabilityparser.model.bandwidth

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.extension.mutableListWithCapacity

class BwsBitMap(bwsBinaryString: String, band: Band, scs: Int, isV1590: Boolean) {
    val bws: IntArray

    init {
        val isFr2 = band > 256

        val bwMap =
            if (isFr2) {
                fr2BwMap
            } else if (isV1590) {
                fr1V1590BwMap
            } else {
                fr1BwMap
            }

        val bwsList = mutableListWithCapacity<Int>(bwMap.size + 1)
        for (i in bwsBinaryString.indices.reversed()) {
            if (bwsBinaryString[i] == '1') {
                bwsList.add(bwMap[i])
            }
        }

        /* According to TS 38.306 v16.6.0 there's no 100MHz field for n41, n48, n77, n78, n79, n90
         * So we assume that it's supported by default:
         * - for scs 30kHz
         * - for scs 60kHz if bwsList isn't empy.
         * Add 100 MHz only for channelBWs (not for its extensions) to avoid duplicates.
         */
        if (
            !isV1590 && band in bandsDefault100 && (scs == 30 || scs == 60 && bwsList.isNotEmpty())
        ) {
            bwsList.add(100)
        }

        bws = bwsList.toIntArray()
    }

    companion object {
        /* According to TS 38.306 v16.6.0 there's no 100MHz field for n41, n48, n77, n78, n79, n90 */
        private val bandsDefault100 = intArrayOf(41, 48, 77, 78, 79, 90)
        private val fr2BwMap = intArrayOf(50, 100, 200)
        private val fr1BwMap = intArrayOf(5, 10, 15, 20, 25, 30, 40, 50, 60, 80)
        private val fr1V1590BwMap = intArrayOf(70, 45, 35, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    }
}
