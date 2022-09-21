package it.smartphonecombo.uecapabilityparser.importer.ltenr

import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities

/**
 * The Class ImportWireshark.
 */
class ImportWireshark : ImportUECapabilityInformation() {
    override val regexSupportedBandCombination: String
        get() {
            val regex = StringBuilder()
            val startRegex = "[\\v\\h]*Item (\\d{1,3})[\\v\\h]*BandCombinationParameters-r10: \\d items?"
            regex.append(startRegex)
            val baseRegex =
                "(?:[\\v\\h]*item[\\v\\h]*\\d{1,3}[\\v\\h]*BandParameters-r10[\\v\\h]*bandEUTRA-r10[\\v\\h]*:[\\v\\h]*(\\d{1,3})(?:[\\v\\h]*bandParametersUL-r10: 1 item[\\v\\h]*item 0[\\v\\h]*CA-MIMO-ParametersUL-r10[\\v\\h]*ca-BandwidthClassUL-r10[\\v\\h]*:[\\v\\h]*([a-z]) \\(\\d\\))?[\\v\\h]*bandParametersDL-r10: 1 item[\\v\\h]*item 0[\\v\\h]*CA-MIMO-ParametersDL-r10[\\v\\h]*ca-BandwidthClassDL-r10[\\v\\h]*:[\\v\\h]*([a-z]) \\(\\d\\)[\\v\\h]*supportedMIMO-CapabilityDL-r10[\\v\\h]*:[\\v\\h]*(two|four|eight)Layers[\\v\\h]*\\(\\d\\))"
            regex.append(baseRegex)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regex.append(baseRegex).append("?")
            }
            return regex.toString()
        }
    override val regexSupportedBandCombinationExt: String
        get() = "Item (\\d{1,3})[\\v\\h]*BandCombinationParametersExt-r10[\\v\\h]*(?:[\\v\\h]*supportedBandwidthCombinationSet-r10[\\v\\h]*:[\\v\\h]*([\\w]{1,8})[\\v\\h]*)?"
    override val regexCA_MIMO_ParametersDL: String
        get() {
            val mimoRegex = StringBuilder()
            val startMimoRegex =
                "Item (\\d{1,3})[\\v\\h]*BandCombinationParameters-v10i0(?:[\\v\\h]*bandParameterList-v10i0: \\d items?)?"
            val baseMimoRegex =
                "(?:[\\v\\h]*Item (\\d)[\\v\\h]*BandParameters-v10i0[\\v\\h]*bandParametersDL-v10i0: 1 item[\\v\\h]* Item \\d[\\v\\h]*CA-MIMO-ParametersDL-v10i0[\\v\\h]*(?:fourLayerTM3-TM4-r10[\\v\\h]*:[\\v\\h]*(supported)[\\v\\h]*\\(0\\))?)"
            mimoRegex.append(startMimoRegex)
            for (i in 0 until ImportCapabilities.lteDlCC) {
                mimoRegex.append(baseMimoRegex).append("?")
            }
            return mimoRegex.toString()
        }
    override val regexBandCombinationParameters_v1090: String
        get() {
            val regexadd = StringBuilder()
            val startRegexAdd = "Item (\\d{1,3})[\\v\\h]*BandCombinationParameters-v1090: \\d items?"
            regexadd.append(startRegexAdd)
            val baseRegexAdd =
                "(?:[\\v\\h]*Item \\d[\\v\\h]*BandParameters-v1090(?:[\\v\\h]*bandEUTRA-v1090 *: *(\\d{1,3}))?)"
            for (i in 0 until ImportCapabilities.lteDlCC) {
                regexadd.append(baseRegexAdd).append("?")
            }
            return regexadd.toString()
        }
    override val regexSupportedBandCombinationAdd: String
        get() {
            val regexadd = StringBuilder()
            val startRegexAdd =
                "[\\v\\h]*Item (\\d{1,3})[\\v\\h]*BandCombinationParameters-r11[\\v\\h]*bandParameterList-r11: \\d items?"
            regexadd.append(startRegexAdd)
            val baseRegexAdd =
                "(?:[\\v\\h]*item[\\v\\h]*\\d{1,3}[\\v\\h]*BandParameters-r11[\\v\\h]*bandEUTRA-r11[\\v\\h]*:[\\v\\h]*(\\d{1,3})(?:[\\v\\h]*bandParametersUL-r11: 1 item[\\v\\h]*item 0[\\v\\h]*CA-MIMO-ParametersUL-r10[\\v\\h]*ca-BandwidthClassUL-r10[\\v\\h]*:[\\v\\h]*([a-z]) \\(\\d\\))?[\\v\\h]*bandParametersDL-r11: 1 item[\\v\\h]*item 0[\\v\\h]*CA-MIMO-ParametersDL-r10[\\v\\h]*ca-BandwidthClassDL-r10[\\v\\h]*:[\\v\\h]*([a-z]) \\(\\d\\)[\\v\\h]*supportedMIMO-CapabilityDL-r10[\\v\\h]*:[\\v\\h]*(two|four|eight)Layers[\\v\\h]*\\(\\d\\))"
            regexadd.append(baseRegexAdd)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regexadd.append(baseRegexAdd).append("?")
            }
            val bcsRegex2 = "(?:[\\v\\h]*supportedBandwidthCombinationSet-r11: (?<bcs>[\\w]{1,8}))?"
            regexadd.append(bcsRegex2)
            return regexadd.toString()
        }
    override val regexSupportedBandCombination_v1430: String
        get() {
            val qam256ul = StringBuilder()
            val start256ulRegex =
                "Item (\\d{1,3})[\\v\\h]*BandCombinationParameters-v1430[\\v\\h]*bandParameterList-v1430: \\d items?"
            val base256ulRegex =
                "(?:[\\v\\h]*Item[\\v\\h]*(\\\\d)[\\v\\h]*BandParameters-v1430(?:[\\v\\h]+ul-256QAM-perCC-InfoList-r14: \\d items?[\\v\\h]*Item 0[\\v\\h]*UL-256QAM-perCC-Info-r14)?(?:[\\v\\h]*ul-256QAM-(?:perCC-)?r14:[\\v\\h]*(supported) \\(0\\))?(?:[\\v\\h]+(?:Item [1-7][\\v\\h]*UL-256QAM-perCC-Info-r14(?:[\\v\\h]*ul-256QAM-(?:perCC-)?r14:[\\v\\h]*supported \\(0\\))?){0,7}))"
            qam256ul.append(start256ulRegex)
            for (i in 0 until ImportCapabilities.lteDlCC) {
                qam256ul.append(base256ulRegex.replace("\\\\d", i.toString() + "")).append("?")
            }
            return qam256ul.toString()
        }
    override val regexSupportedBandCombination_v1530: String
        get() {
            val qam1024 = StringBuilder()
            val start1024Regex =
                "Item (\\d{1,3})[\\v\\h]*BandCombinationParameters-v1530[\\v\\h]*bandParameterList-v1530: \\d items?"
            val base1024Regex =
                "(?:[\\v\\h]*Item[\\v\\h]*(\\\\d)[\\v\\h]*BandParameters-v1530(?:[\\v\\h]*dl-1024QAM-r15:[\\v\\h]*(supported) \\(0\\))?)"
            qam1024.append(start1024Regex)
            for (i in 0 until ImportCapabilities.lteDlCC) {
                qam1024.append(base1024Regex.replace("\\\\d", i.toString() + "")).append("?")
            }
            return qam1024.toString()
        }
    override val regexBandCombinationReduced: String
        get() {
            val regexReduced = StringBuilder()
            val startRegexReduced =
                "[\\v\\h]*Item (\\d{1,3})[\\v\\h]*BandCombinationParameters-r13[\\v\\h]*(?:differentFallbackSupported-r13:[\\v\\h]*true[\\v\\h]*\\(0\\)[\\v\\h]*)?bandParameterList-r13: \\d items?"
            regexReduced.append(startRegexReduced)
            val baseRegexReduced =
                ("(?:[\\v\\h]*item[\\v\\h]*\\d{1,3}[\\v\\h]*BandParameters-r13[\\v\\h]*bandEUTRA-r13[\\v\\h]*:[\\v\\h]*(\\d{1,3})(?:[\\v\\h]*bandParametersUL-r13[\\v\\h]*ca-BandwidthClassUL-r10[\\v\\h]*:[\\v\\h]*([a-z]) \\(\\d\\))?[\\v\\h]*bandParametersDL-r13[\\v\\h]*ca-BandwidthClassDL-r13[\\v\\h]*:[\\v\\h]*([a-z]) \\(\\d\\)[\\v\\h]*supportedMIMO-CapabilityDL-r13[\\v\\h]*:[\\v\\h]*(two|four|eight)Layers[\\v\\h]*\\(\\d\\)(?:[\\v\\h]*fourLayerTM3-TM4-r13: supported \\(0\\))?"
                        + "[\\v\\h]*intraBandContiguousCC-InfoList-r13: \\d items?(?:[\\v\\h]*item \\d[\\v\\h]*IntraBandContiguousCC-Info-r12)+)")
            regexReduced.append(baseRegexReduced)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regexReduced.append(baseRegexReduced).append("?")
            }
            val bcsRegex3 = "(?:[\\v\\h]*supportedBandwidthCombinationSet-r13: (?<bcs>[\\w]{1,8}))?"
            regexReduced.append(bcsRegex3)
            return regexReduced.toString()
        }
    override val regexSingleBands: String
        get() = "Item (\\d{1,3})[\\v\\h]*SupportedBandEUTRA[\\v\\h]*bandEUTRA\\s?:\\s?(\\d{1,3})"
    override val regexSupportedBandListEUTRA_v9e0: String
        get() = "Item (\\d{1,3})[\\v\\h]*SupportedBandEUTRA-v9e0[\\v\\h]*(?:bandEUTRA-v9e0\\s?:\\s?(\\d{1,3}))?"
    override val regexSupportedBandListEUTRA_v1250: String
        get() = "Item (\\d{1,3})[\\v\\h]*SupportedBandEUTRA-v1250[\\v\\h]*(?:dl-256QAM-r12\\s?:\\s?(supported)[\\v\\h]*\\(0\\))?[\\v\\h]*(?:ul-64QAM-r12\\s?:\\s?(supported)[\\v\\h]*\\(0\\))?"
    override val regexSupportedBandListEN_DC: String
        get() = "Item (\\d{1,3})[\\v\\h]*SupportedBandNR-r15[\\v\\h]*bandNR-r15\\s?:\\s?(\\d{1,3})"
    override val regexSupportedBandListNR_SA: String
        get() = regexSupportedBandListEN_DC
    override val regexUECategory: String
        get() = "ue-Category(DL|UL)*[-\\w]*\\s*:\\s*n?(\\d*)"
    override val regexNRFeatureSetPerCC: String
        get() = "featureSet(Downlink|Uplink)PerCC\\s*supportedSubcarrierSpacing(?:DL|UL)\\s*:\\skHz(\\d*)\\s*\\(\\d*\\)\\s*supportedBandwidth(?:DL|UL)\\s*:\\s*fr\\d\\s*\\(\\d*\\)\\s*fr\\d\\s*:\\s*mhz(\\d*)\\s*\\(\\d*\\)(?:\\s*channelBW-90mhz:\\s(\\w*)\\s\\(\\d\\)\\s*)?[\\w\\d\\s:-]*(?:maxNumberMIMO-Layers(?:CB\\-)?P[UD]SCH\\s*:\\s*(\\w*)Layers?\\s*\\(\\d*\\))?[\\w\\d\\s:-]*(?:maxNumberMIMO-LayersNon(?:CB\\-)?P[UD]SCH\\s*:\\s*\\w*Layers?\\s*\\(\\d*\\))?\\s*supportedModulationOrder(?:DL|UL)\\s*:\\s*(\\w*)\\s*\\(\\d*\\)"
    override val regexNRFeatureSetPerCCList: String
        get() {
            val regex = StringBuilder(
                "featureSetListPer(Downlink|Uplink)CC:\\s*(\\d*)\\s*items?"
            )
            val mainRegex = "(?:\\s*Item (\\\\d)\\s*FeatureSet(?:Downlink|Uplink)PerCC-id\\s*:\\s*(\\d*))"
            regex.append(mainRegex.replace("\\\\d", "0"))
            for (i in 1 until ImportCapabilities.nrDlCC) {
                regex.append(mainRegex.replace("\\\\d", i.toString() + "")).append("?")
            }
            return regex.toString()
        }
    override val regexNrCombos: String
        get() {
            val regex = StringBuilder(
                "Item (\\d{1,3})\\s*BandCombination\\s*bandList: \\d items?"
            )
            val baseRegex = ("(?:\\s*Item \\\\d"
                    + "\\s*BandParameters: (?:eutra|nr) \\(\\d\\)\\s*(eutra|nr)"
                    + "\\s*band(?:EUTRA|NR): (\\d{1,3})"
                    + "\\s*ca-BandwidthClassDL-(?:EUTRA|NR): ([a-z]) \\(\\d\\)"
                    + "(?:\\s*ca-BandwidthClassUL-(?:EUTRA|NR): ([a-z]) \\(\\d\\))?)")
            regex.append(baseRegex.replace("\\\\d", "0"))
            for (i in 1 until ImportCapabilities.nrDlCC) {
                regex.append(baseRegex.replace("\\\\d", i.toString() + "")).append("?")
            }
            regex.append("\\s*featureSetCombination: (?<featureset>\\d{1,3})")
            return regex.toString()
        }
    override val regexFeatureSetCombinations: String
        get() {
            val regex = StringBuilder(
                "Item (\\d{1,3})\\s*FeatureSetCombination: \\d items?"
            )
            val baseRegexFeature = ("(?:\\s*Item \\\\d\\s*FeatureSetsPerBand: \\d* items?\\s*Item \\d*"
                    + "\\s*FeatureSet: (?:eutra|nr) \\(\\d\\)\\s*(eutra|nr)"
                    + "\\s*downlinkSet(?:EUTRA|NR): (\\d{1,3})"
                    + "\\s*uplinkSet(?:EUTRA|NR): (\\d{1,3})"
                    + "(?:\\s*Item \\d*\\s*FeatureSet: (?:eutra|nr) \\(\\d\\)\\s*(?:eutra|nr)\\s*downlinkSet(?:EUTRA|NR): (?:\\d{1,3})\\s*uplinkSet(?:EUTRA|NR): (?:\\d{1,3}))*)")
            regex.append(baseRegexFeature.replace("\\\\d", "0"))
            for (i in 1 until ImportCapabilities.nrDlCC) {
                regex.append(baseRegexFeature.replace("\\\\d", i.toString() + "")).append("?")
            }
            return regex.toString()
        }
    override val regexSupportedBandListNR: String
        get() {
            val regex = StringBuilder("Item (\\d{1,3})[\\v\\h]*BandNR[\\v\\h]*bandNR: (\\d{1,3})")
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*pdsch-256QAM-FR2: (supported) \\(\\d\\))?")
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*pusch-256QAM: (supported) \\(\\d\\))?")
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*ue-PowerClass: pc(\\d) \\(\\d\\))?")
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*rateMatchingLTE-CRS: (supported) \\(\\d\\))?")
            val scs = "(?:scs-(\\d*)kHz:\\s[0-9a-f]*\\s\\[.*,\\s([01]+[01\\s]*).*\\][\\v\\h]*)?"
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*channelBWs-DL: fr[1-2] \\(\\d\\)[\\v\\h]*fr[1-2][\\v\\h]*")
            regex.append(scs).append(scs).append(scs).append(")?")
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*channelBWs-UL: fr[1-2] \\(\\d\\)[\\v\\h]*fr[1-2][\\v\\h]*")
            regex.append(scs).append(scs).append(scs).append(")?")
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*maxUplinkDutyCycle(?:-PC2)?-(FR[1-2]): n(\\d{1,3}))?")
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*channelBWs-DL-v1590: fr[1-2] \\(\\d\\)[\\v\\h]*fr[1-2][\\v\\h]*")
            regex.append(scs).append(scs).append(scs).append(")?")
            regex.append("(?:(?:(?!Item \\d{1,3}\\s*BandNR)[\\S\\v\\h])*channelBWs-UL-v1590: fr[1-2] \\(\\d\\)[\\v\\h]*fr[1-2][\\v\\h]*")
            regex.append(scs).append(scs).append(scs).append(")?")
            return regex.toString()
        }
    override val regexLTEFeatureSetPerCC: String
        get() = "featureSet(UL|DL)-PerCC-r15(?:\\s*fourLayerTM3-TM4-r15: supported \\(0\\))?(?:\\s*supportedMIMO-Capability(?:DL|UL)(?:-MRDC)?-r15\\s*:\\s*(\\w*)Layers?\\s*\\(\\d*\\))?(?:\\s*(?:ul|dl)-(\\w*QAM)-r15\\s*:\\s*supported\\s*\\(\\d*\\))?"
    override val regexLTEFeatureSetPerCCList: String
        get() {
            val regex = StringBuilder(
                "featureSetPerCC-List(DL|UL)-r15:\\s*(\\d*)\\s*items?"
            )
            val mainRegex = "(?:\\s*Item (\\\\d)\\s*FeatureSet(?:DL|UL)-PerCC-id-r15\\s*:\\s*(\\d*))"
            regex.append(mainRegex.replace("\\\\d", "0"))
            for (i in 1 until ImportCapabilities.nrDlCC) {
                regex.append(mainRegex.replace("\\\\d", i.toString() + "")).append("?")
            }
            return regex.toString()
        }
}