package it.smartphonecombo.uecapabilityparser.bean.nr

/*
 * Don't update! It's used to obtain the default BW without channelBWs or SupportedBandwidth
 *
 * Source:
 * 3GPP TS 38.101-1 version 15.7.0 Release 15
 * 3GPP TS 38.101-2 version 15.7.0 Release 15
 */
object BwTableNr {
    private val fr1Common =
        arrayOf(
            BwNr(15, intArrayOf(20, 15, 10, 5)),
            BwNr(30, intArrayOf(20, 15, 10)),
            BwNr(60, intArrayOf(20, 15, 10))
        )
    private val fr1LowBandCommon =
        arrayOf(BwNr(15, intArrayOf(20, 15, 10, 5)), BwNr(30, intArrayOf(20, 15, 10)))
    private val fr1HighBandCommon =
        arrayOf(
            BwNr(15, intArrayOf(50, 40, 20, 15, 10)),
            BwNr(30, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10)),
            BwNr(60, intArrayOf(100, 80, 60, 50, 40, 20, 15, 10))
        )
    private val fr2Common =
        arrayOf(BwNr(60, intArrayOf(200, 100, 50)), BwNr(120, intArrayOf(200, 100, 50)))
    private val data =
        mapOf(
            Pair(1, fr1Common),
            Pair(2, fr1Common),
            Pair(
                3,
                arrayOf(
                    BwNr(15, intArrayOf(30, 25, 20, 15, 10, 5)),
                    BwNr(30, intArrayOf(30, 25, 20, 15, 10)),
                    BwNr(60, intArrayOf(30, 25, 20, 15, 10))
                )
            ),
            Pair(5, fr1LowBandCommon),
            Pair(7, fr1Common),
            Pair(8, fr1LowBandCommon),
            Pair(12, arrayOf(BwNr(15, intArrayOf(15, 10, 5)), BwNr(30, intArrayOf(15, 10)))),
            Pair(20, fr1LowBandCommon),
            Pair(25, fr1Common),
            Pair(28, fr1LowBandCommon),
            Pair(
                34,
                arrayOf(
                    BwNr(15, intArrayOf(15, 10, 5)),
                    BwNr(30, intArrayOf(15, 10)),
                    BwNr(60, intArrayOf(15, 10))
                )
            ),
            Pair(38, fr1Common),
            Pair(
                39,
                arrayOf(
                    BwNr(15, intArrayOf(40, 30, 25, 20, 15, 10, 5)),
                    BwNr(30, intArrayOf(40, 30, 25, 20, 15, 10)),
                    BwNr(60, intArrayOf(40, 30, 25, 20, 15, 10))
                )
            ),
            Pair(
                40,
                arrayOf(
                    BwNr(15, intArrayOf(50, 40, 30, 25, 20, 15, 10, 5)),
                    BwNr(30, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10)),
                    BwNr(60, intArrayOf(80, 60, 50, 40, 30, 25, 20, 15, 10))
                )
            ),
            Pair(41, fr1HighBandCommon),
            Pair(
                50,
                arrayOf(
                    BwNr(15, intArrayOf(50, 40, 20, 15, 10, 5)),
                    BwNr(
                        30,
                        intArrayOf(80, 60, 50, 40, 20, 15, 10),
                        intArrayOf(60, 50, 40, 20, 15, 10)
                    ),
                    BwNr(
                        60,
                        intArrayOf(80, 60, 50, 40, 20, 15, 10),
                        intArrayOf(60, 50, 40, 20, 15, 10)
                    )
                )
            ),
            Pair(51, arrayOf(BwNr(15, intArrayOf(5)))),
            Pair(
                66,
                arrayOf(
                    BwNr(15, intArrayOf(40, 20, 15, 10, 5)),
                    BwNr(30, intArrayOf(40, 20, 15, 10)),
                    BwNr(60, intArrayOf(40, 20, 15, 10))
                )
            ),
            Pair(
                70,
                arrayOf(
                    BwNr(15, intArrayOf(25, 20, 15, 10, 5), intArrayOf(15, 10, 5)),
                    BwNr(30, intArrayOf(25, 20, 15, 10), intArrayOf(15, 10)),
                    BwNr(60, intArrayOf(25, 20, 15, 10), intArrayOf(15, 10))
                )
            ),
            Pair(71, fr1LowBandCommon),
            Pair(74, fr1Common),
            Pair(
                75,
                arrayOf(
                    BwNr(15, intArrayOf(20, 15, 10, 5), IntArray(0)),
                    BwNr(30, intArrayOf(20, 15, 10), IntArray(0)),
                    BwNr(60, intArrayOf(20, 15, 10), IntArray(0))
                )
            ),
            Pair(76, arrayOf(BwNr(15, intArrayOf(5), IntArray(0)))),
            Pair(77, fr1HighBandCommon),
            Pair(78, fr1HighBandCommon),
            Pair(
                79,
                arrayOf(
                    BwNr(15, intArrayOf(50, 40)),
                    BwNr(30, intArrayOf(100, 80, 60, 50, 40)),
                    BwNr(60, intArrayOf(100, 80, 60, 50, 40))
                )
            ),
            Pair(
                80,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(30, 25, 20, 15, 10, 5)),
                    BwNr(30, IntArray(0), intArrayOf(30, 25, 20, 15, 10)),
                    BwNr(60, IntArray(0), intArrayOf(30, 25, 20, 15, 10))
                )
            ),
            Pair(
                81,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(20, 15, 10, 5)),
                    BwNr(30, IntArray(0), intArrayOf(20, 15, 10))
                )
            ),
            Pair(
                82,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(20, 15, 10, 5)),
                    BwNr(30, IntArray(0), intArrayOf(20, 15, 10))
                )
            ),
            Pair(
                83,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(20, 15, 10, 5)),
                    BwNr(30, IntArray(0), intArrayOf(20, 15, 10))
                )
            ),
            Pair(
                84,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(20, 15, 10, 5)),
                    BwNr(30, IntArray(0), intArrayOf(20, 15, 10)),
                    BwNr(60, IntArray(0), intArrayOf(20, 15, 10))
                )
            ),
            Pair(
                86,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(40, 20, 15, 10, 5)),
                    BwNr(30, IntArray(0), intArrayOf(40, 20, 15, 10)),
                    BwNr(60, IntArray(0), intArrayOf(40, 20, 15, 10))
                )
            ),
            Pair(257, fr2Common),
            Pair(258, fr2Common),
            Pair(260, fr2Common),
            Pair(261, fr2Common)
        )

    fun getDLBws(nrBand: Int, scs: Int): BwNr {
        return getDLBws(nrBand).firstOrNull { x: BwNr -> x.scs == scs } ?: BwNr(scs, IntArray(0))
    }

    private fun getDLBws(nrBand: Int): Array<BwNr> {
        return data.getOrDefault(nrBand, emptyArray())
    }
}
