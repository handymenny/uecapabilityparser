package it.smartphonecombo.uecapabilityparser.util

import it.smartphonecombo.uecapabilityparser.model.Rat
import java.io.*
import kotlin.system.exitProcess

class Tshark {
    private val config: Config = Config
    fun startDecoder(strEncodedData: String, strProtocol: String, ratType: Rat): String {
        if (ratType != Rat.eutra && ratType != Rat.eutra_nr && ratType != Rat.nr) {
            throw RuntimeException()
        }
        val strData = Utility.preformatHexData(strEncodedData)
        val strBuilder: StringBuilder?
        if (!(strData[0] == '3' && strData[1] < 'F' && strData[1] >= '8')) {
            val length = String.format("%X", strData.length / 2 + 32768)
            strBuilder = StringBuilder("3A01").append(ratType.id)
            strBuilder!!.append(length).append(strData)
        } else {
            strBuilder = StringBuilder(strData)
        }
        if (strBuilder.length % 2 == 1) {
            strBuilder.append('0')
        }
        try {
            val tempfile = File.createTempFile("UECAP-", ".pcap")
            val writer = BufferedOutputStream(FileOutputStream(tempfile))
            val pcap = PcapWriter()
            writer.write(Utility.hexStringToByteArray(pcap.all))
            val length = String.format("%08X", strBuilder.length / 2)
            writer.write(Utility.hexStringToByteArray(length + length))
            writer.write(Utility.hexStringToByteArray(strBuilder.toString()))
            writer.flush()
            writer.close()
            val result = callTshark(tsharkPath, strProtocol, tempfile.path)
            tempfile.delete()
            return result
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    /** Call Tshark to decode the PCAP file. */
    private fun callTshark(
        strTsharkPath: String,
        strProtocol: String,
        pcapFileName: String
    ): String {
        val tsharkCmd = strTsharkPath + "tshark"
        var userdltsString =
            ("uat:user_dlts:\"User 0 (DLT=147)\",\"" + strProtocol + "\",\"0\",\"\",\"0\",\"\"")
        if (OsType.CURRENT == OsType.WINDOWS) {
            userdltsString = userdltsString.replace("\"".toRegex(), "\\\\\"")
        }
        val str = StringBuilder()
        var line: String?
        try {
            val p =
                Runtime.getRuntime()
                    .exec(arrayOf(tsharkCmd, "-o", userdltsString, "-r", pcapFileName, "-V", "-l"))
            val bri = BufferedReader(InputStreamReader(p.inputStream, Charsets.UTF_8))
            val bre = BufferedReader(InputStreamReader(p.errorStream, Charsets.UTF_8))
            var i = 0
            while (bri.readLine().also { line = it } != null) {
                i++
                if (i > 15) {
                    str.append(line).append('\n')
                }
            }
            bri.close()
            while (bre.readLine().also { line = it } != null) {
                System.err.println(line)
            }
            bre.close()
            if (p.waitFor() == 1) {
                exitProcess(1)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return str.toString()
    }

    private val tsharkPath: String
        get() = config.getOrDefault("TsharkPath", "")

    class PcapWriter {
        private val magicNumber = 2712847316L
        private val versionMajor = 2
        private val versionMinor = 4
        private val zone = 0
        private val sigFigs = 0
        private val snapLen = 88192
        private val network = 147
        private val tsSec = 0
        private val tsUsec = 0
        val all =
            String.format("%08x", magicNumber) +
                String.format("%04x", versionMajor) +
                String.format("%04x", versionMinor) +
                String.format("%08x", zone) +
                String.format("%08x", sigFigs) +
                String.format("%08x", snapLen) +
                String.format("%08x", network) +
                String.format("%08x", tsSec) +
                String.format("%08x", tsUsec)
    }
}
