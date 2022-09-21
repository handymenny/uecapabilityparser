package it.smartphonecombo.uecapabilityparser.importer.ltenr

import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities

/**
 * The Class ImportNsg.
 */
class ImportCellularPro : ImportUECapabilityInformation() {
    init {
        super.supportBandCombination = "SupportedBandCombination-r10"
        super.supportBandCombinationAdd = "SupportedBandCombinationAdd-r11"
        super.supportBandCombinationReduced = "supportedBandCombinationReduced-r13"
        super.mimo4BandCombination = "supportedMIMO-CapabilityDL-r10 : 1"
        //Not supported :(
        super.supportedBandListEUTRA_v9e0 = null
    }

    override val regexSupportedBandCombination: String
        get() {
            val regex = StringBuilder()
            val startRegex = "[\\v\\h]BandCombinationParameters-r10()"
            regex.append(startRegex)
            val baseRegex = ("(?:[\\v\\h]*BandParameters-r10"
                    + "[\\v\\h]*bandEUTRA-r10 : (\\d{1,3})"
                    + "(?:[\\v\\h]*bandParametersUL-r10 : [\\v\\h]*BandParametersUL-r10[\\v\\h]*CA-MIMO-ParametersUL-r10"
                    + "[\\v\\h]*ca-BandwidthClassUL-r10 : \\d \\(([a-z])\\))?"
                    + "[\\v\\h]*bandParametersDL-r10 : [\\v\\h]*BandParametersDL-r10[\\v\\h]*CA-MIMO-ParametersDL-r10"
                    + "[\\v\\h]*ca-BandwidthClassDL-r10 : \\d \\(([a-z])\\)"
                    + "[\\v\\h]*(?:supportedMIMO-CapabilityDL-r10 : \\d \\((two|four|eight)Layers)\\))")
            regex.append(baseRegex)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regex.append(baseRegex).append("?")
            }
            return regex.toString()
        }
    override val regexSupportedBandCombinationExt: String
        get() = ("BandCombinationParametersExt-r10()"
                + "(?:[\\v\\h]*supportedBandwidthCombinationSet-r10 : ([\\w]{1,4})?)?")

    //Not supported :(
    override val regexCA_MIMO_ParametersDL: String
        get() =//Not supported :(
            ""
    override val regexBandCombinationParameters_v1090: String
        get() {
            val regexadd = StringBuilder()
            val startRegexAdd = "[\\v\\h]BandCombinationParameters-v1090()"
            regexadd.append(startRegexAdd)
            val baseRegexAdd = "(?:[\\v\\h]*BandParameters-v1090(?:[\\v\\h]*bandEUTRA-v1090 : (\\d{1,3}))?)"
            for (i in 0 until ImportCapabilities.lteDlCC) {
                regexadd.append(baseRegexAdd).append("?")
            }
            return regexadd.toString()
        }
    override val regexSupportedBandCombinationAdd: String
        get() {
            val regexadd = StringBuilder()
            val startRegexAdd = "[\\v\\h]bandParameterList-r11"
            regexadd.append(startRegexAdd)
            val baseRegexAdd = ("(?:[\\v\\h]*"
                    + "[\\v\\h]*bandEUTRA-r11 : (\\d{1,3})"
                    + "(?:[\\v\\h]*bandParametersUL-r11 : [\\v\\h]*BandParametersUL-r10"
                    + "[\\v\\h]*ca-BandwidthClassUL-r10 : ([a-z]))?"
                    + "[\\v\\h]*bandParametersDL-r11 : [\\v\\h]*BandParametersDL-r10"
                    + "[\\v\\h]*ca-BandwidthClassDL-r10 : \\d \\(([a-z])\\)"
                    + "[\\v\\h]*(?:supportedMIMO-CapabilityDL-r10 : \\d \\((two|four|eight)Layers)\\))")
            regexadd.append(baseRegexAdd)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regexadd.append(baseRegexAdd).append("?")
            }
            val bcsRegex2 = "(?:[\\v\\h]*supportedBandwidthCombinationSet-r11 : (?<bcs>[\\w]{1,4}))?"
            regexadd.append(bcsRegex2)
            return regexadd.toString()
        }
    override val regexSupportedBandCombination_v1430: String
        get() {
            val qam256ul = StringBuilder()
            val start256ulRegex =
                "BandCombinationParameters-v1430()[\\v\\h]*(?:bandParameterList-v1430 : bandParameterList-v1430)?"
            val base256ulRegex =
                "(?:[\\v\\h]*BandParameters-v1430()(?:[\\v\\h]+ul-256QAM-perCC-InfoList-r14 : ul-256QAM-perCC-InfoList-r14[\\v\\h]*UL-256QAM-perCC-Info-r14)?(?:[\\v\\h]*ul-256QAM-(?:perCC-)?r14 : 0 \\((supported)\\))?(?:[\\v\\h]+(?:[\\v\\h]*UL-256QAM-perCC-Info-r14(?:[\\v\\h]*ul-256QAM-(?:perCC-)?r14 : 0 \\((supported)\\))?){0,7}))"
            qam256ul.append(start256ulRegex)
            for (i in 0 until ImportCapabilities.lteDlCC) {
                qam256ul.append(base256ulRegex).append("?")
            }
            return qam256ul.toString()
        }
    override val regexSupportedBandCombination_v1530: String
        get() {
            val qam1024 = StringBuilder()
            val start1024Regex =
                "BandCombinationParameters-v1530()[\\v\\h]*(?:bandParameterList-v1530 : bandParameterList-v1530)?"
            val base1024Regex = "(?:[\\v\\h]*BandParameters-v1530()(?:[\\v\\h]*dl-1024QAM-r15 : 0 \\((supported)\\))?)"
            qam1024.append(start1024Regex)
            for (i in 0 until ImportCapabilities.lteDlCC) {
                qam1024.append(base1024Regex).append("?")
            }
            return qam1024.toString()
        }
    override val regexBandCombinationReduced: String
        get() {
            val regexReduced = StringBuilder()
            val startRegexReduced =
                "[\\v\\h]BandCombinationParameters-r13()[\\v\\h]*(?:differentFallbackSupported-r13 : 0 \\(true\\)[\\v\\h]*)?bandParameterList-r13 : bandParameterList-r13"
            regexReduced.append(startRegexReduced)
            val baseRegexReduced =
                ("(?:[\\v\\h]*BandParameters-r13[\\v\\h]*bandEUTRA-r13[\\v\\h]*:[\\v\\h]*(\\d{1,3})(?:[\\v\\h]*bandParametersUL-r13 : bandParametersUL-r13[\\v\\h]*ca-BandwidthClassUL-r10[\\v\\h]*:[\\v\\h]*\\d \\(([a-z])\\))?[\\v\\h]*bandParametersDL-r13 : bandParametersDL-r13[\\v\\h]*ca-BandwidthClassDL-r13[\\v\\h]*:[\\v\\h]*\\d \\(([a-z])\\)[\\v\\h]*supportedMIMO-CapabilityDL-r13[\\v\\h]*:[\\v\\h]*\\d \\((two|four|eight)Layers\\)(?:[\\v\\h]*fourLayerTM3-TM4-r13 : 0 \\(supported\\))?"
                        + "[\\v\\h]*intraBandContiguousCC-InfoList-r13 : intraBandContiguousCC-InfoList-r13(?:[\\v\\h]*IntraBandContiguousCC-Info-r12)+)")
            regexReduced.append(baseRegexReduced)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regexReduced.append(baseRegexReduced).append("?")
            }
            val bcsRegex3 = "(?:[\\v\\h]*supportedBandwidthCombinationSet-r13 : (?<bcs>[\\w]{1,4}))?"
            regexReduced.append(bcsRegex3)
            return regexReduced.toString()
        }
    override val regexSingleBands: String
        get() = "SupportedBandEUTRA()[\\v\\h]*bandEUTRA\\s?:\\s?(\\d{1,3})"

    // Not Supported :(
    override val regexSupportedBandListEUTRA_v9e0: String
        get() =// Not Supported :(
            ""
    override val regexSupportedBandListEUTRA_v1250: String
        get() = "SupportedBandEUTRA-v1250()[\\v\\h]*(?:dl-256QAM-r12\\s?:\\s? 0 \\((supported)\\))?[\\v\\h]*(?:ul-64QAM-r12\\s?:\\s? 0 \\((supported)\\))?"
    override val regexSupportedBandListEN_DC: String
        get() = "SupportedBandNR-r15()[\\v\\h]*bandNR-r15\\s?:\\s?(\\d{1,3})"
    override val regexSupportedBandListNR_SA: String
        get() = regexSupportedBandListEN_DC
    override val regexUECategory: String
        get() = "ue-Category(DL|UL)*[-\\w]*\\s*:\\s*n?(\\d*)"

    // TODO Auto-generated method stub
    override val regexNRFeatureSetPerCC: String
        get() =// TODO Auto-generated method stub
            ""

    // TODO Auto-generated method stub
    override val regexNRFeatureSetPerCCList: String
        get() =// TODO Auto-generated method stub
            ""

    // TODO Auto-generated method stub
    override val regexNrCombos: String
        get() =// TODO Auto-generated method stub
            ""

    // TODO Auto-generated method stub
    override val regexFeatureSetCombinations: String
        get() =// TODO Auto-generated method stub
            ""
    override val regexSupportedBandListNR: String
        get() = ""

    // TODO Auto-generated method stub
    override val regexLTEFeatureSetPerCC: String?
        get() =// TODO Auto-generated method stub
            null

    // TODO Auto-generated method stub
    override val regexLTEFeatureSetPerCCList: String?
        get() =// TODO Auto-generated method stub
            null
}