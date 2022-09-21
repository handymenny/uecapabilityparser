package it.smartphonecombo.uecapabilityparser.importer.lte

import com.mindprod.ledatastream.LERandomAccessFile
import it.smartphonecombo.uecapabilityparser.bean.Capabilities
import it.smartphonecombo.uecapabilityparser.bean.IComponent
import it.smartphonecombo.uecapabilityparser.bean.lte.ComboLte
import it.smartphonecombo.uecapabilityparser.bean.lte.ComponentLte
import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities
import java.io.EOFException
import java.io.IOException

class ImportNvItem : ImportCapabilities {
    //private int maxstr = 0;
    //private static final //LOGGER //LOGGER = LogManager.get//LOGGER();
    private var `in`: LERandomAccessFile? = null

    override fun parse(filename: String): Capabilities {
        `in` = null
        var lteComponents = emptyArray<IComponent>()
        val listCombo = ArrayList<ComboLte>()
        try {
            `in` = LERandomAccessFile(filename, "r")

            //System.out.println("Input file size: " + in.length() + " bytes");
            //System.out.println("Format version: " + in.readUnsignedShort());
            //System.out.println("Number of descriptors: " + in.readUnsignedShort() + "\n");
            `in`!!.skipBytes(4)
            while (true) {
                try {
                    when (`in`!!.readUnsignedShort()) {
                        333 -> {
                            lteComponents = readDLbands(true, 7)
                            if (lteComponents.isEmpty()) {
                                //LOGGER.error("Incorrect format: no any downlink carrier in combo "+ format);
                                return Capabilities()
                            }
                        }

                        334 -> {
                            val combo = readULbands(lteComponents, true, 7)
                            //System.out.println(combo);
                            listCombo.add(combo)
                        }

                        201 -> {
                            lteComponents = readDLbands(true, 0)
                            if (lteComponents.isEmpty()) {
                                //LOGGER.error("Incorrect format: no any downlink carrier in combo "+ format);
                                return Capabilities()
                            }
                        }

                        202 -> {
                            val combo = readULbands(lteComponents, true, 0)
                            //System.out.println(combo);
                            listCombo.add(combo)
                        }

                        137 -> {
                            lteComponents = readDLbands(false, 0)
                            if (lteComponents.isEmpty()) {
                                //LOGGER.error("Incorrect format: no any downlink carrier in combo "+ format);
                                return Capabilities()
                            }
                        }

                        138 -> {
                            val combo = readULbands(lteComponents, false, 0)
                            // System.out.println(combo);
                            listCombo.add(combo)
                        }

                        else -> {}
                    }
                } catch (ex: EOFException) {
                    //System.out.println("End of file");
                    break
                }
            }
            //LOGGER.info("Number of combos: " + numcom);
            ////LOGGER.info("Max streams per combo: " + maxstr);
        } catch (e: Exception) {
            //LOGGER.fatal("Fatal error: " + e.getLocalizedMessage());
            //System.out.println(e.getStackTrace());
        } finally {
            try {
                `in`!!.close()
            } catch (e: IOException) {
                //LOGGER.fatal("Fatal error: " + e.getLocalizedMessage());
                //e.printStackTrace();
            }
        }
        // System.out.println(listCombo);
        return Capabilities(listCombo)
    }

    @Throws(IOException::class)
    private fun readDLbands(mimoPresent: Boolean, additionalBytes: Int): Array<IComponent> {
        val lteComponents: MutableList<ComponentLte> = ArrayList()
        for (i in 0 until ImportCapabilities.lteDlCC) {
            val band = `in`!!.readUnsignedShort()
            val bclass = (`in`!!.readUnsignedByte() + 0x40).toChar()
            var ant = 2
            if (mimoPresent) {
                ant = `in`!!.readUnsignedByte()
            }
            `in`!!.skipBytes(additionalBytes)
            if (band != 0) {
                lteComponents.add(ComponentLte(band, bclass, '0', ant, null, null))
            }
        }
        lteComponents.sortWith(IComponent.defaultComparator.reversed())
        //System.out.println(Arrays.toString(bandArray));
        return lteComponents.toTypedArray()
    }

    @Throws(IOException::class)
    private fun readULbands(
        dlBands: Array<IComponent>, mimoPresent: Boolean,
        additionalBytes: Int
    ): ComboLte {
        val copyBand = emptyArray<IComponent>()
        var i = 0
        while (i < dlBands.size) {
            copyBand[i] = (dlBands[i] as ComponentLte).copy()
            i++
        }
        val numberOfDLbands = copyBand.size
        i = 0
        while (i < numberOfDLbands) {
            val band = `in`!!.readUnsignedShort()
            val ulClass = (`in`!!.readUnsignedByte() + 0x40).toChar()
            var ant = 1
            if (mimoPresent) {
                ant = `in`!!.readUnsignedByte()
            }
            `in`!!.skipBytes(additionalBytes)
            if (band != 0) {
                for (dlBand in copyBand) {
                    if (band == dlBand.band) {
                        dlBand.classUL = ulClass
                        break
                    }
                }
            }
            i++
        }
        while (i < ImportCapabilities.lteDlCC) {
            `in`!!.skipBytes(additionalBytes + 3 + if (mimoPresent) 1 else 0)
            i++
        }
        return ComboLte(copyBand)
    }
}