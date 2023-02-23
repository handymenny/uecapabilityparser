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
            BwNr(15, intArrayOf(5, 10, 15, 20)),
            BwNr(30, intArrayOf(10, 15, 20)),
            BwNr(60, intArrayOf(10, 15, 20))
        )
    private val fr1LowBandCommon =
        arrayOf(BwNr(15, intArrayOf(5, 10, 15, 20)), BwNr(30, intArrayOf(10, 15, 20)))
    private val fr1HighBandCommon =
        arrayOf(
            BwNr(15, intArrayOf(10, 15, 20, 40, 50)),
            BwNr(30, intArrayOf(10, 15, 20, 40, 50, 60, 80, 100)),
            BwNr(60, intArrayOf(10, 15, 20, 40, 50, 60, 80, 100))
        )
    private val fr2Common =
        arrayOf(BwNr(60, intArrayOf(50, 100, 200)), BwNr(120, intArrayOf(50, 100, 200)))
    private val data =
        mapOf(
            Pair(1, fr1Common),
            Pair(2, fr1Common),
            Pair(
                3,
                arrayOf(
                    BwNr(15, intArrayOf(5, 10, 15, 20, 25, 30)),
                    BwNr(30, intArrayOf(10, 15, 20, 25, 30)),
                    BwNr(60, intArrayOf(10, 15, 20, 25, 30))
                )
            ),
            Pair(5, fr1LowBandCommon),
            Pair(7, fr1Common),
            Pair(8, fr1LowBandCommon),
            Pair(12, arrayOf(BwNr(15, intArrayOf(5, 10, 15)), BwNr(30, intArrayOf(10, 15)))),
            Pair(20, fr1LowBandCommon),
            Pair(25, fr1Common),
            Pair(28, fr1LowBandCommon),
            Pair(
                34,
                arrayOf(
                    BwNr(15, intArrayOf(5, 10, 15)),
                    BwNr(30, intArrayOf(10, 15)),
                    BwNr(60, intArrayOf(10, 15))
                )
            ),
            Pair(38, fr1Common),
            Pair(
                39,
                arrayOf(
                    BwNr(15, intArrayOf(5, 10, 15, 20, 25, 30, 40)),
                    BwNr(30, intArrayOf(10, 15, 20, 25, 30, 40)),
                    BwNr(60, intArrayOf(10, 15, 20, 25, 30, 40))
                )
            ),
            Pair(
                40,
                arrayOf(
                    BwNr(15, intArrayOf(5, 10, 15, 20, 25, 30, 40, 50)),
                    BwNr(30, intArrayOf(10, 15, 20, 25, 30, 40, 50, 60, 80)),
                    BwNr(60, intArrayOf(10, 15, 20, 25, 30, 40, 50, 60, 80))
                )
            ),
            Pair(41, fr1HighBandCommon),
            Pair(
                50,
                arrayOf(
                    BwNr(15, intArrayOf(5, 10, 15, 20, 40, 50)),
                    BwNr(
                        30,
                        intArrayOf(10, 15, 20, 40, 50, 60, 80),
                        intArrayOf(10, 15, 20, 40, 50, 60)
                    ),
                    BwNr(
                        60,
                        intArrayOf(10, 15, 20, 40, 50, 60, 80),
                        intArrayOf(10, 15, 20, 40, 50, 60)
                    )
                )
            ),
            Pair(51, arrayOf(BwNr(15, intArrayOf(5)))),
            Pair(
                66,
                arrayOf(
                    BwNr(15, intArrayOf(5, 10, 15, 20, 40)),
                    BwNr(30, intArrayOf(10, 15, 20, 40)),
                    BwNr(60, intArrayOf(10, 15, 20, 40))
                )
            ),
            Pair(
                70,
                arrayOf(
                    BwNr(15, intArrayOf(5, 10, 15, 20, 25), intArrayOf(5, 10, 15)),
                    BwNr(30, intArrayOf(10, 15, 20, 25), intArrayOf(10, 15)),
                    BwNr(60, intArrayOf(10, 15, 20, 25), intArrayOf(10, 15))
                )
            ),
            Pair(71, fr1LowBandCommon),
            Pair(74, fr1Common),
            Pair(
                75,
                arrayOf(
                    BwNr(15, intArrayOf(5, 10, 15, 20), IntArray(0)),
                    BwNr(30, intArrayOf(10, 15, 20), IntArray(0)),
                    BwNr(60, intArrayOf(10, 15, 20), IntArray(0))
                )
            ),
            Pair(76, arrayOf(BwNr(15, intArrayOf(5), IntArray(0)))),
            Pair(77, fr1HighBandCommon),
            Pair(78, fr1HighBandCommon),
            Pair(
                79,
                arrayOf(
                    BwNr(15, intArrayOf(40, 50)),
                    BwNr(30, intArrayOf(40, 50, 60, 80, 100)),
                    BwNr(60, intArrayOf(40, 50, 60, 80, 100))
                )
            ),
            Pair(
                80,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(5, 10, 15, 20, 25, 30)),
                    BwNr(30, IntArray(0), intArrayOf(10, 15, 20, 25, 30)),
                    BwNr(60, IntArray(0), intArrayOf(10, 15, 20, 25, 30))
                )
            ),
            Pair(
                81,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(5, 10, 15, 20)),
                    BwNr(30, IntArray(0), intArrayOf(10, 15, 20))
                )
            ),
            Pair(
                82,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(5, 10, 15, 20)),
                    BwNr(30, IntArray(0), intArrayOf(10, 15, 20))
                )
            ),
            Pair(
                83,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(5, 10, 15, 20)),
                    BwNr(30, IntArray(0), intArrayOf(10, 15, 20))
                )
            ),
            Pair(
                84,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(5, 10, 15, 20)),
                    BwNr(30, IntArray(0), intArrayOf(10, 15, 20)),
                    BwNr(60, IntArray(0), intArrayOf(10, 15, 20))
                )
            ),
            Pair(
                86,
                arrayOf(
                    BwNr(15, IntArray(0), intArrayOf(5, 10, 15, 20, 40)),
                    BwNr(30, IntArray(0), intArrayOf(10, 15, 20, 40)),
                    BwNr(60, IntArray(0), intArrayOf(10, 15, 20, 40))
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
