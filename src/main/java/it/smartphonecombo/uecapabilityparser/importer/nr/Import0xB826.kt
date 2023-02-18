package it.smartphonecombo.uecapabilityparser.importer.nr

import com.mindprod.ledatastream.LERandomAccessFile
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.EOFException
import java.io.IOException

class Import0xB826 : ImportCapabilities {
    override fun parse(filename: String): Capabilities {
        var `in`: LERandomAccessFile? = null
        val combos = Capabilities()
        val listCombo = ArrayList<ComboNr>()
        var endc = false
        try {
            `in` = LERandomAccessFile(filename, "r")
            var fileSize = `in`.readUnsignedShort().toLong()
            if (fileSize == `in`.length()) {
                val logItem = "0x" + Integer.toHexString(`in`.readUnsignedShort()).uppercase()
                combos.setMetadata("logItem", logItem)
                if (debug) {
                    println("Log Item: $logItem")
                }
                `in`.skipBytes(8)
            } else {
                fileSize = `in`.length()
                `in`.seek(0)
            }
            combos.setMetadata("logSize", fileSize)
            if (debug) {
                println("Log file size: $fileSize bytes")
            }

            val version = `in`.readUnsignedShort()
            combos.setMetadata("version", version)
            if (debug) {
                println("Version $version\n")
            }

            `in`.skipBytes(2)
            var numCombos = `in`.readUnsignedShort()
            if (version > 3) {
                if (debug) {
                    println("Total Numb Combos $numCombos\n")
                }
                combos.setMetadata("totalCombos", numCombos)
                val index = `in`.readUnsignedShort()
                combos.setMetadata("index", index)
                if (debug) {
                    println("Index $index\n")
                }
                numCombos = `in`.readUnsignedShort()
                `in`.skipBytes(1)
            }
            if (debug) {
                println("Num Combos $numCombos\n")
            }
            combos.setMetadata("numCombos", numCombos)
            var comboN = 0
            while (comboN < numCombos) {
                try {
                    if (version >= 8) {
                        `in`.skipBytes(3)
                    }
                    var numBands = `in`.readUnsignedByte()
                    if (version > 2) {
                        numBands = numBands ushr 1
                    }
                    if (version >= 8) {
                        numBands = numBands ushr 2
                        numBands = numBands and 0x0F
                    }
                    val bands = ArrayList<IComponent>()
                    val nrbands = ArrayList<IComponent>()
                    if (version >= 6) {
                        `in`.skipBytes(1)
                        if (version == 7) {
                            `in`.skipBytes(2)
                        }
                        if (version >= 9) {
                            `in`.skipBytes(8)
                        }
                        if (version >= 14) {
                            `in`.skipBytes(16)
                        }
                    }
                    for (i in 0 until numBands) {
                        var band: Int
                        val mixed = `in`.readUnsignedShort()
                        var temp: Int
                        if (version >= 8) {
                            temp = mixed ushr 9 and 0x1F
                            band = mixed and 0x1FF
                        } else {
                            band = mixed
                            temp = `in`.readUnsignedByte()
                        }
                        var bwclass = ((temp ushr 1) + 0x40).toChar()
                        if (bwclass < 'A') bwclass = '\u0000'
                        if (temp % 2 == 1) {
                            val nrband = ComponentNr(band)
                            nrband.classDL = bwclass
                            if (version >= 8) {
                                temp = `in`.readUnsignedByte()
                                var mimo = temp shl 1
                                mimo = mimo and 0x7F
                                mimo += mixed ushr 15
                                nrband.mimoDL = getMimoFromIndex(mimo)
                            } else {
                                nrband.mimoDL = getMimoFromIndex(`in`.readUnsignedByte())
                            }
                            var mimoUL = 0
                            if (version >= 8) {
                                temp = temp ushr 6
                                val temp2 = `in`.readUnsignedByte()
                                temp += temp2 shl 2
                                temp = temp and 0x1F
                                mimoUL = (temp2 ushr 3) and 0x7F
                            } else {
                                temp = `in`.readUnsignedByte() ushr 1
                            }
                            if (temp > 0) nrband.classUL = (temp + 0x40).toChar()
                            if (version < 8) {
                                mimoUL = `in`.readUnsignedByte()
                            }
                            nrband.mimoUL = getMimoFromIndex(mimoUL)
                            temp = `in`.readUnsignedByte()
                            var modUL = temp
                            if (version >= 8) {
                                modUL = modUL shr 1
                                modUL = modUL and 0x3
                            }
                            nrband.modUL = getQamFromIndex(modUL)
                            if (version < 8) `in`.skipBytes(1)
                            if (version >= 6) {
                                var scsIndex = temp
                                if (version >= 8) {
                                    temp = `in`.readUnsignedByte()
                                    scsIndex = scsIndex ushr 7
                                    scsIndex += temp and 3 shl 1
                                } else {
                                    temp = `in`.readUnsignedShort()
                                    scsIndex = temp
                                }
                                nrband.scs = 15 * (1 shl (scsIndex and 0x000F) - 1)
                                if (version >= 8) {
                                    val maxBWindex = temp shr 2 and 0x1F
                                    nrband.maxBandwidth = getBWFromIndexV8(maxBWindex)
                                    `in`.skipBytes(2)
                                } else {
                                    nrband.maxBandwidth = getBWFromIndex(temp ushr 6 and 0x1F)
                                }
                            } else {
                                if (version > 2) {
                                    nrband.scs = 15 * (1 shl `in`.readUnsignedByte() - 1)
                                } else {
                                    nrband.scs = 15 * (1 + `in`.readUnsignedByte())
                                }
                                nrband.maxBandwidth = `in`.readUnsignedByte() shl 2
                            }
                            nrbands.add(nrband)
                        } else {
                            endc = true
                            val lteband = ComponentLte()
                            lteband.band = band
                            lteband.classDL = bwclass
                            if (version >= 8) {
                                temp = `in`.readUnsignedByte()
                                var mimo = temp shl 1
                                mimo = mimo and 0x7F
                                mimo += mixed ushr 15
                                lteband.mimoDL = getMimoFromIndex(mimo)
                            } else {
                                lteband.mimoDL = getMimoFromIndex(`in`.readUnsignedByte())
                            }
                            if (version >= 8) {
                                temp = temp ushr 6
                                val temp2 = `in`.readUnsignedByte()
                                temp += temp2 shl 2
                                temp = temp and 0x1F
                            } else {
                                temp = `in`.readUnsignedByte() ushr 1
                            }
                            if (temp > 0) lteband.classUL = (temp + 0x40).toChar()
                            if (version < 8) {
                                /* LTE UL MIMO isn't useful */
                                `in`.skipBytes(1)
                            }
                            temp = `in`.readUnsignedByte()
                            var modUL = temp
                            if (version >= 8) {
                                modUL = modUL shr 1
                                modUL = modUL and 0x3
                            }
                            lteband.modUL = getQamFromIndex(modUL)
                            `in`.skipBytes(3)
                            bands.add(lteband)
                        }
                    }
                    val bandArray = bands
                        .sortedWith(IComponent.defaultComparator.reversed())
                        .toTypedArray()
                    val nrbandsArray = nrbands
                        .sortedWith(IComponent.defaultComparator.reversed())
                        .toTypedArray()
                    val newCombo = if (bandArray.isEmpty()) {
                        ComboNr(nrbandsArray)
                    } else {
                        ComboNr(bandArray, nrbandsArray)
                    }
                    listCombo.add(newCombo)
                    comboN++
                } catch (ex: EOFException) {
                    break
                }
            }
            if (debug) {
                println(listCombo)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (endc) {
            combos.enDcCombos = listCombo
        } else {
            combos.nrCombos = listCombo
        }
        return combos
    }

    private fun getMimoFromIndex(index: Int): Int {
        return when (index) {
            0 -> 0
            1, 25, 16, 9, 4 -> 1
            2, 42, 56, 72, 26, 27, 28, 29, 30, 17, 18, 19, 20, 10, 11, 12, 5, 6 -> 2
            3, 31, 32, 33, 34, 35, 21, 22, 23, 24, 13, 14, 15, 7, 8 -> 4
            else -> index
        }
    }

    private fun getQamFromIndex(index: Int): String {
        return when (index) {
            2, 5 -> "256qam"
            3, 6 -> "1024qam"
            else -> "64qam"
        }
    }

    private fun getBWFromIndexV8(index: Int): Int {
        return when (index) {
            0 -> 5
            1, 2 -> 10
            3 -> 15
            4, 5 -> 20
            8, 9 -> 25
            10 -> 30
            11 -> 40
            12, 13 -> 50
            17 -> 60
            18 -> 70
            19, 20 -> 80
            21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 -> 100
            else -> index
        }
    }

    private fun getBWFromIndex(index: Int): Int {
        return when (index) {
            4 -> 5
            5 -> 10
            6 -> 15
            7 -> 20
            8 -> 25
            9 -> 30
            10 -> 40
            11, 15 -> 50
            12 -> 60
            13 -> 80
            14, 20, 21, 22, 23, 24, 25, 26 -> 100
            else -> index
        }
    }
}