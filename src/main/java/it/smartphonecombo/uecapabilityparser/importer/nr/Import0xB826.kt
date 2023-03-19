package it.smartphonecombo.uecapabilityparser.importer.nr

import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.bean.nr.ComboNr
import it.smartphonecombo.uecapabilityparser.bean.nr.ComponentNr
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedByte
import it.smartphonecombo.uecapabilityparser.extension.readUnsignedShort
import it.smartphonecombo.uecapabilityparser.extension.skipBytes
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Import0xB826 : ImportCapabilities {
    override fun parse(filename: String): Capabilities {
        val combos = Capabilities()
        val listCombo = ArrayList<ComboNr>()
        var endc = false
        var nrdc = false
        try {
            val file = File(filename)
            val byteArray = file.inputStream().use { it.readAllBytes() }
            val input = ByteBuffer.wrap(byteArray)
            input.order(ByteOrder.LITTLE_ENDIAN)
            var fileSize = input.readUnsignedShort()
            if (fileSize == input.limit()) {
                val logItem = "0x" + Integer.toHexString(input.readUnsignedShort()).uppercase()
                combos.setMetadata("logItem", logItem)
                if (debug) {
                    println("Log Item: $logItem")
                }
                input.skipBytes(8)
            } else {
                fileSize = input.limit()
                input.rewind()
            }
            combos.setMetadata("logSize", fileSize)
            if (debug) {
                println("Log file size: $fileSize bytes")
            }

            val version = input.readUnsignedShort()
            combos.setMetadata("version", version)
            if (debug) {
                println("Version $version\n")
            }

            input.skipBytes(2)
            var numCombos = input.readUnsignedShort()
            if (version > 3) {
                if (debug) {
                    println("Total Numb Combos $numCombos\n")
                }
                combos.setMetadata("totalCombos", numCombos)
                val index = input.readUnsignedShort()
                combos.setMetadata("index", index)
                if (debug) {
                    println("Index $index\n")
                }
                numCombos = input.readUnsignedShort()
            }
            if (debug) {
                println("Num Combos $numCombos\n")
            }
            combos.setMetadata("numCombos", numCombos)

            var source: String? = null
            if (version > 3) {
                // Parse source field
                val sourceIndex = input.readUnsignedByte()
                source = getSourceFromIndex(sourceIndex)
                combos.setMetadata("source", source)
                if (debug) {
                    println("source $source\n")
                }
            }

            var comboN = 0
            while (comboN < numCombos && input.remaining() > 0) {
                if (version >= 8) {
                    input.skipBytes(3)
                }
                var numBands = input.readUnsignedByte()
                if (version > 2) {
                    numBands = numBands ushr 1
                }
                if (version >= 8) {
                    numBands = numBands ushr 2
                    numBands = numBands and 0x0F
                }
                val bands = mutableListOf<IComponent>()
                var nrbands = mutableListOf<IComponent>()
                var nrdcbands = mutableListOf<IComponent>()
                if (version >= 6) {
                    input.skipBytes(1)
                    if (version == 7) {
                        input.skipBytes(2)
                    }
                    if (version >= 9) {
                        input.skipBytes(8)
                    }
                    if (version >= 14) {
                        input.skipBytes(16)
                    }
                }
                for (i in 0 until numBands) {
                    var band: Int
                    val mixed = input.readUnsignedShort()
                    var temp: Int
                    if (version >= 8) {
                        temp = mixed ushr 9 and 0x1F
                        band = mixed and 0x1FF
                    } else {
                        band = mixed
                        temp = input.readUnsignedByte()
                    }
                    var bwclass = ((temp ushr 1) + 0x40).toChar()
                    if (bwclass < 'A') bwclass = '\u0000'
                    if (temp % 2 == 1) {
                        val nrband = ComponentNr(band)
                        nrband.classDL = bwclass
                        if (version >= 8) {
                            temp = input.readUnsignedByte()
                            var mimo = temp shl 1
                            mimo = mimo and 0x7F
                            mimo += mixed ushr 15
                            nrband.mimoDL = getMimoFromIndex(mimo)
                        } else {
                            nrband.mimoDL = getMimoFromIndex(input.readUnsignedByte())
                        }
                        var mimoUL = 0
                        if (version >= 8) {
                            temp = temp ushr 6
                            val temp2 = input.readUnsignedByte()
                            temp += temp2 shl 2
                            temp = temp and 0x1F
                            mimoUL = (temp2 ushr 3) and 0x7F
                        } else {
                            temp = input.readUnsignedByte() ushr 1
                        }
                        if (temp > 0) nrband.classUL = (temp + 0x40).toChar()
                        if (version < 8) {
                            mimoUL = input.readUnsignedByte()
                        }
                        nrband.mimoUL = getMimoFromIndex(mimoUL)
                        temp = input.readUnsignedByte()
                        var modUL = temp
                        if (version >= 8) {
                            modUL = modUL shr 1
                            modUL = modUL and 0x3
                        }
                        nrband.modUL = getQamFromIndex(modUL)
                        if (version < 8) input.skipBytes(1)
                        if (version >= 6) {
                            var scsIndex = temp
                            if (version >= 8) {
                                temp = input.readUnsignedByte()
                                scsIndex = scsIndex ushr 7
                                scsIndex += temp and 3 shl 1
                            } else {
                                temp = input.readUnsignedShort()
                                scsIndex = temp
                            }
                            nrband.scs = 15 * (1 shl (scsIndex and 0x000F) - 1)
                            if (version >= 8) {
                                val maxBWindex = temp shr 2 and 0x1F
                                nrband.maxBandwidth = getBWFromIndexV8(maxBWindex)
                                input.skipBytes(2)
                            } else {
                                nrband.maxBandwidth = getBWFromIndex(temp ushr 6 and 0x1F)
                            }
                        } else {
                            if (version > 2) {
                                nrband.scs = 15 * (1 shl input.readUnsignedByte() - 1)
                            } else {
                                nrband.scs = 15 * (1 + input.readUnsignedByte())
                            }
                            nrband.maxBandwidth = input.readUnsignedByte() shl 2
                        }
                        nrbands.add(nrband)
                    } else {
                        endc = true
                        val lteband = ComponentLte()
                        lteband.band = band
                        lteband.classDL = bwclass
                        if (version >= 8) {
                            temp = input.readUnsignedByte()
                            var mimo = temp shl 1
                            mimo = mimo and 0x7F
                            mimo += mixed ushr 15
                            lteband.mimoDL = getMimoFromIndex(mimo)
                        } else {
                            lteband.mimoDL = getMimoFromIndex(input.readUnsignedByte())
                        }
                        if (version >= 8) {
                            temp = temp ushr 6
                            val temp2 = input.readUnsignedByte()
                            temp += temp2 shl 2
                            temp = temp and 0x1F
                        } else {
                            temp = input.readUnsignedByte() ushr 1
                        }
                        if (temp > 0) lteband.classUL = (temp + 0x40).toChar()
                        if (version < 8) {
                            /* LTE UL MIMO isn't useful */
                            input.skipBytes(1)
                        }
                        temp = input.readUnsignedByte()
                        var modUL = temp
                        if (version >= 8) {
                            modUL = modUL shr 1
                            modUL = modUL and 0x3
                        }
                        lteband.modUL = getQamFromIndex(modUL)
                        input.skipBytes(3)
                        bands.add(lteband)
                    }
                }
                /*
                 * We assume that 0xb826 without explicit combo type in source
                 * don't support NR CA FR1-FR2.
                 */
                if (!endc && !source.equals("RF_NRCA")) {
                    val (fr2bands, fr1bands) = nrbands.partition { (it as ComponentNr).isFR2 }

                    if (fr2bands.isNotEmpty() && fr1bands.isNotEmpty()) {
                        nrdc = true
                        nrbands = fr1bands.toMutableList()
                        nrdcbands = fr2bands.toMutableList()
                    }
                }

                val bandArray =
                    bands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
                val nrbandsArray =
                    nrbands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
                val nrdcbandsArray =
                    nrdcbands.sortedWith(IComponent.defaultComparator.reversed()).toTypedArray()
                val newCombo =
                    if (endc) {
                        ComboNr(bandArray, nrbandsArray)
                    } else if (nrdc) {
                        ComboNr(nrbandsArray, nrdcbandsArray)
                    } else {
                        ComboNr(nrbandsArray)
                    }
                listCombo.add(newCombo)
                comboN++
            }
            if (debug) {
                println(listCombo)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (endc) {
            combos.enDcCombos = listCombo
        } else if (nrdc) {
            combos.nrDcCombos = listCombo
        } else {
            combos.nrCombos = listCombo
        }
        return combos
    }

    private fun getMimoFromIndex(index: Int): Int {
        return when (index) {
            0 -> 0
            1,
            25,
            16,
            9,
            4 -> 1
            2,
            42,
            56,
            72,
            26,
            27,
            28,
            29,
            30,
            17,
            18,
            19,
            20,
            10,
            11,
            12,
            5,
            6 -> 2
            3,
            31,
            32,
            33,
            34,
            35,
            21,
            22,
            23,
            24,
            13,
            14,
            15,
            7,
            8 -> 4
            else -> index
        }
    }

    private fun getQamFromIndex(index: Int): String {
        return when (index) {
            2,
            5 -> "256qam"
            3,
            6 -> "1024qam"
            else -> "64qam"
        }
    }

    private fun getBWFromIndexV8(index: Int): Int {
        return when (index) {
            0 -> 5
            1,
            2 -> 10
            3 -> 15
            4,
            5 -> 20
            8,
            9 -> 25
            10 -> 30
            11 -> 40
            12,
            13 -> 50
            17 -> 60
            18 -> 70
            19,
            20 -> 80
            21,
            22,
            23,
            24,
            25,
            26,
            27,
            28,
            29,
            30,
            31 -> 100
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
            11,
            15 -> 50
            12 -> 60
            13 -> 80
            14,
            20,
            21,
            22,
            23,
            24,
            25,
            26 -> 100
            else -> index
        }
    }

    private fun getSourceFromIndex(index: Int): String {
        return when (index) {
            0 -> "RF"
            1 -> "PM"
            3 -> "RF_ENDC"
            4 -> "RF_NRCA"
            5 -> "RF_NRDC"
            else -> index.toString()
        }
    }
}
