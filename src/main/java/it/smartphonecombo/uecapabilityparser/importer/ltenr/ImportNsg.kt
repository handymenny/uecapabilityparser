package it.smartphonecombo.uecapabilityparser.importer.ltenr

import it.smartphonecombo.uecapabilityparser.importer.ImportCapabilities

/**
 * The Class ImportNsg.
 */
class ImportNsg : ImportUECapabilityInformation() {
    init {
        super.mimo4BandCombination = "supportedMIMO-CapabilityDL-r10 : fourLayers"
    }

    override val regexSupportedBandCombination: String
        get() {
            val regex = StringBuilder()
            val startRegex = "[\\v\\h](?:SupportedBandCombination-r10)?[\\v\\h]*\\[(\\d{1,3})]"
            regex.append(startRegex)
            val baseRegex = ("(?:[\\v\\h]*(?:BandCombinationParameters-r10)?\\[\\d]"
                    + "[\\v\\h]*bandEUTRA-r10 : (\\d{1,3})"
                    + "(?:[\\v\\h]*bandParametersUL-r10[\\v\\h]*(?:BandParametersUL-r10)?\\[\\d]"
                    + "[\\v\\h]*ca-BandwidthClassUL-r10 : ([a-z]))?"
                    + "[\\v\\h]*bandParametersDL-r10[\\v\\h]*(?:BandParametersDL-r10)?\\[\\d]"
                    + "[\\v\\h]*ca-BandwidthClassDL-r10 : ([a-z])"
                    + "[\\v\\h]*(?:supportedMIMO-CapabilityDL-r10 : (two|four|eight)Layers))")
            regex.append(baseRegex)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regex.append(baseRegex).append("?")
            }
            return regex.toString()
        }
    override val regexSupportedBandCombinationExt: String
        get() = ("(?:SupportedBandCombinationExt-r10)?\\[(\\d{1,3})\\][\\v\\h]*"
                + "supportedBandwidthCombinationSet-r10 : '?([\\w\\s]*)'?(\\w)?")
    override val regexCA_MIMO_ParametersDL: String
        get() {
            val mimoRegex = StringBuilder()
            val startMimoRegex = "[\\v\\h](?:supportedBandCombination-v10i0)?[\\v\\h]*\\[(\\d{1,3})\\]"
            val baseMimoRegex =
                "(?:[\\v\\h]*(?:bandParameterList-v10i0)?[\\v\\h]*\\[(\\d)\\][\\v\\h]*bandParametersDL-v10i0[\\v\\h]*\\[0\\](?:[\\v\\h]*fourLayerTM3-TM4-r10[\\v\\h]*:[\\v\\h]*(supported))?)"
            mimoRegex.append(startMimoRegex).append(baseMimoRegex)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                mimoRegex.append(baseMimoRegex).append("?")
            }
            return mimoRegex.toString()
        }
    override val regexBandCombinationParameters_v1090: String
        get() {
            val regexadd = StringBuilder()
            val startRegexAdd = "[\\v\\h](?:SupportedBandCombination-v1090)?[\\v\\h]*\\[(\\d{1,3})\\]"
            regexadd.append(startRegexAdd)
                .append("(?:[\\v\\h]*(?:BandCombinationParameters-v1090)?\\[0](?:[\\v\\h]*bandEUTRA-v1090 *: *(\\d{1,3}))?)")
            val baseRegexAdd =
                "(?:[\\v\\h]*(?:BandCombinationParameters-v1090)?\\[\\\\d\\](?![\\v\\h]*\\[0\\])(?:[\\v\\h]*bandEUTRA-v1090 *: *(\\d{1,3}))?)"
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regexadd.append(baseRegexAdd.replace("\\\\d", i.toString() + "")).append("?")
            }
            return regexadd.toString()
        }
    override val regexSupportedBandCombinationAdd: String
        get() {
            val regexadd = StringBuilder()
            val startRegexAdd = "[\\v\\h](?:SupportedBandCombinationAdd-r11)?[\\v\\h]*\\[(\\d{1,3})\\]"
            regexadd.append(startRegexAdd)
            val baseRegexAdd = ("(?:[\\v\\h]*(?:bandParameterList-r11)?[\\v\\h]*\\[\\d\\]"
                    + "[\\v\\h]*bandEUTRA-r11 : (\\d{1,3})"
                    + "(?:[\\v\\h]*bandParametersUL-r11(?:[\\v\\h]*BandParametersUL-r10)?[\\v\\h]*\\[\\d]"
                    + "[\\v\\h]*ca-BandwidthClassUL-r10 : ([a-z]))?"
                    + "[\\v\\h]*bandParametersDL-r11(?:[\\v\\h]*BandParametersDL-r10)?[\\v\\h]*\\[\\d]"
                    + "[\\v\\h]*ca-BandwidthClassDL-r10 : ([a-z])"
                    + "[\\v\\h]*(?:supportedMIMO-CapabilityDL-r10 : (two|four|eight)Layers))")
            regexadd.append(baseRegexAdd)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regexadd.append(baseRegexAdd).append("?")
            }
            val bcsRegex2 =
                "(?:[\\v\\h]*supportedBandwidthCombinationSet-r11 : '?(?<bcs>[\\w]{1,8})'?(?<bcsUnit>\\w)?)?"
            regexadd.append(bcsRegex2)
            return regexadd.toString()
        }
    override val regexSupportedBandCombination_v1430: String
        get() {
            val qam256ul = StringBuilder()
            val start256ulRegex = "(?:supportedBandCombination(?:Reduced|Add)?-v1430)?\\[(\\d{1,3})\\]"
            val base256ulRegex =
                "(?:[\\v\\h]*(?:bandParameterList-v1430[\\v\\h]*)?\\[(\\\\d)\\](?![\\v\\h]*bandParameterList-v1430[\\v\\h]+)(?:[\\v\\h]+ul-256QAM-perCC-InfoList-r14[\\v\\h]*\\[0\\])?(?:[\\v\\h]*ul-256QAM-(?:perCC-)?r14[\\v\\h]*:[\\v\\h]*(supported))?(?:[\\v\\h]+(?:ul-256QAM-perCC-InfoList-r14)?[\\v\\h]*\\[[1-7]\\](?![\\v\\h]*ul-256QAM-r14|[\\v\\h]*ul-256QAM-perCC-InfoList-r14)[\\v\\h]*(?![\\v\\h]*bandParameterList-v1430[\\v\\h]+)(?:ul-256QAM-perCC-r14[\\v\\h]*:[\\v\\h]*supported)?){0,7})"
            qam256ul.append(start256ulRegex)
            for (i in 0 until ImportCapabilities.lteDlCC) {
                qam256ul.append(base256ulRegex.replace("\\\\d", i.toString() + "")).append("?")
            }
            return qam256ul.toString()
        }
    override val regexSupportedBandCombination_v1530: String
        get() {
            val qam1024 = StringBuilder()
            val start1024Regex = "(?:supportedBandCombination(?:Reduced|Add)?-v1530)?\\[(\\d{1,3})\\]"
            val base1024Regex =
                "(?:[\\v\\h]*(?:bandParameterList-v1530[\\v\\h]*)?\\[(\\d)\\](?![\\v\\h]*bandParameterList-v1530[\\v\\h]+)(?:[\\v\\h]*dl-1024QAM-r15[\\v\\h]*:[\\v\\h]*(supported))?)"
            qam1024.append(start1024Regex)
            for (i in 0 until ImportCapabilities.lteDlCC) {
                qam1024.append(base1024Regex).append("?")
            }
            return qam1024.toString()
        }
    override val regexBandCombinationReduced: String
        get() {
            val regexReduced = StringBuilder()
            val startRegexReduced = "[\\v\\h](?:supportedBandCombinationReduced-r13)?[\\v\\h]*\\[(\\d{1,3})\\]"
            regexReduced.append(startRegexReduced)
            val baseRegexReduced =
                ("(?:[\\v\\h]*(?:differentFallbackSupported-r13[\\v\\h]*:[\\v\\h]*true[\\v\\h]*)?(?:bandParameterList-r13)?[\\v\\h]*\\[\\d\\]"
                        + "[\\v\\h]*bandEUTRA-r13 : (\\d{1,3})"
                        + "(?:[\\v\\h]*bandParametersUL-r13"
                        + "[\\v\\h]*ca-BandwidthClassUL-r10 : ([a-z]))?"
                        + "[\\v\\h]*bandParametersDL-r13"
                        + "[\\v\\h]*ca-BandwidthClassDL-r13 : ([a-z])"
                        + "(?:[\\v\\h]*supportedMIMO-CapabilityDL-r13 : (two|four|eight)Layers)?"
                        + "(?:[\\v\\h]*fourLayerTM3-TM4-r13 : (supported))?"
                        + "(?:[\\v\\h]*(?:intraBandContiguousCC-InfoList-r13)?[\\v\\h]*\\[[0-7]\\](?![\\v\\h]*bandEUTRA-r13|bandParameterList)[\\v\\h]*){0,7})")
            regexReduced.append(baseRegexReduced)
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regexReduced.append(baseRegexReduced).append("?")
            }
            val bcsRegex3 =
                "(?:[\\v\\h]*supportedBandwidthCombinationSet-r13 : '?(?<bcs>[\\w]{1,8})'?(?<bcsUnit>\\w)?)?"
            regexReduced.append(bcsRegex3)
            return regexReduced.toString()
        }
    override val regexSingleBands: String
        get() = "(?:supportedBandListEUTRA)?[\\v\\h]*\\[(\\d{1,3})\\][\\v\\h]*bandEUTRA\\s?:\\s?(\\d{1,3})"
    override val regexSupportedBandListEUTRA_v9e0: String
        get() = "(?:supportedBandListEUTRA-v9e0)?[\\v\\h]*\\[(\\d{1,3})\\][\\v\\h]*(?:bandEUTRA-v9e0\\s?:\\s?(\\d{1,3}))?"
    override val regexSupportedBandListEUTRA_v1250: String
        get() = "(?:supportedBandListEUTRA-v1250)?[\\v\\h]*\\[(\\d{1,3})\\][\\v\\h]*(?:dl-256QAM-r12\\s?:\\s?(supported))?[\\v\\h]*(?:ul-64QAM-r12\\s?:\\s?(supported))?"
    override val regexSupportedBandListEN_DC: String
        get() = "(?:supportedBandListEN-DC-r15)?[\\v\\h]*\\[(\\d{1,3})\\][\\v\\h]*bandNR-r15\\s?:\\s?(\\d{1,3})"
    override val regexSupportedBandListNR_SA: String
        get() = "(?:supportedBandListNR-SA-r15)?[\\v\\h]*\\[(\\d{1,3})\\][\\v\\h]*bandNR-r15\\s?:\\s?(\\d{1,3})"
    override val regexUECategory: String
        get() = "ue-Category(DL|UL)*[-\\w]*\\s*:\\s*n?(\\d*)"
    override val regexNRFeatureSetPerCC: String
        get() = "(?:featureSets(Downlink|Uplink)PerCC)?[\\v\\h]*\\[\\d*\\][\\v\\h]*supportedSubcarrierSpacing(?:DL|UL)[\\v\\h]:[\\v\\h]kHz(\\d*)[\\v\\h]*supportedBandwidth(?:DL|UL)[\\v\\h]->[\\v\\h]fr\\d[\\v\\h]:[\\v\\h]mhz(\\d*)[\\v\\h]*(?:channelBW-90mhz[\\v\\h]:[\\v\\h](\\w*)[\\v\\h]*)?(?:mimo-CB-PUSCH)?(?:[\\v\\h]*maxNumberMIMO-Layers(?:CB-)?P[UD]SCH[\\v\\h]:[\\v\\h](\\w*)Layers?)?[\\w\\d[\\v\\h]:-]*supportedModulationOrder(?:DL|UL)[\\v\\h]:[\\v\\h](\\w*)"
    override val regexNRFeatureSetPerCCList: String
        get() {
            val regex = StringBuilder("(?:featureSets(Downlink|Uplink))?[\\v\\h]*\\[(\\d*)\\]")
            val mainRegex =
                "(?:[\\v\\h]*(?:featureSetListPer(?:Downlink|Uplink)CC)?[\\v\\h]*\\[(\\\\d)\\][\\v\\h]:[\\v\\h](\\d*))"
            regex.append(mainRegex.replace("\\\\d", "0"))
            for (i in 1 until ImportCapabilities.nrDlCC) {
                regex.append(mainRegex.replace("\\\\d", i.toString() + "")).append("?")
            }
            return regex.toString()
        }
    override val regexNrCombos: String
        get() {
            val regex = StringBuilder(
                "(?:supportedBandCombinationList)?\\[(\\d{1,3})\\]"
            )
            val baseRegex = ("(?:[\\v\\h]*(?:bandList)?[\\v\\h]*\\[\\\\d](?: -> )?" + "[\\v\\h]*(eutra|nr)"
                    + "[\\v\\h]*band(?:EUTRA|NR) : (\\d{1,3})"
                    + "(?:[\\v\\h]*ca-BandwidthClassDL-(?:EUTRA|NR) : ([a-z]))?"
                    + "(?:[\\v\\h]*ca-BandwidthClassUL-(?:EUTRA|NR) : ([a-z]))?)")
            regex.append(baseRegex.replace("\\\\d", "0").replace("(?:bandList)?", "(?:bandList)"))
            for (i in 1 until ImportCapabilities.nrDlCC) {
                regex.append(baseRegex.replace("\\\\d", i.toString() + "")).append("?")
            }
            regex.append("[\\v\\h]*featureSetCombination : (?<featureset>\\d{1,3})")
            return regex.toString()
        }
    override val regexFeatureSetCombinations: String
        get() {
            val regex = StringBuilder(
                "(?:featureSetCombinations)?\\[(\\d{1,3})\\]"
            )

            val regexFeature = StringBuilder("(?:[\\v\\h]*\\[\\\\d]")

            val baseRegexFeature = ("(?:[\\v\\h]*\\[\\\\d](?: -> )?" + "[\\v\\h]*(eutra|nr)"
                    + "[\\v\\h]*downlinkSet(?:EUTRA|NR) : (\\d{1,3})"
                    + "[\\v\\h]*uplinkSet(?:EUTRA|NR) : (\\d{1,3}))")

            // The real max is 128, but that would be too slow...
            regexFeature.append(baseRegexFeature.replace("\\\\d", "0"))
            for (i in 1 until 32) {
                val baseRegex = baseRegexFeature.replace("\\\\d", i.toString() + "")
                regexFeature.append(baseRegex).append("?")
            }

            regex.append(regexFeature.toString().replace("\\\\d", "0")).append(")")
            for (i in 1 until ImportCapabilities.nrDlCC) {
                val baseRegex = regexFeature.toString().replace("\\\\d", i.toString() + "")
                regex.append(baseRegex).append(")?")
            }
            return regex.toString()
        }
    override val regexSupportedBandListNR: String
        get() {
            val regex = StringBuilder("\\[(\\d{1,3})\\][\\s]*bandNR : (\\d{1,3})")
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*pdsch-256QAM-FR2 : (supported))?")
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*pusch-256QAM : (supported))?")
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*ue-PowerClass : pc(\\d))?")
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*rateMatchingLTE-CRS : (supported))?")
            val scs = "(?:scs-(\\d*)kHz : '([01 ]*)'B\\(\\d*\\)[\\s]*)?"
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*channelBWs-DL -> fr[1-2][\\s]*")
            regex.append(scs).append(scs).append(scs).append(")?")
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*channelBWs-UL -> fr[1-2][\\s]*")
            regex.append(scs).append(scs).append(scs).append(")?")
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*maxUplinkDutyCycle(?:-PC2)?-(FR[1-2]): n(\\d{1,3}))?")
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*channelBWs-DL-v1590 -> fr[1-2][\\s]*")
            regex.append(scs).append(scs).append(scs).append(")?")
            regex.append("(?:(?:(?!\\[\\d{1,3}\\][\\s]*bandNR)[\\S\\s])*channelBWs-UL-v1590 -> fr[1-2][\\s]*")
            regex.append(scs).append(scs).append(scs).append(")?")
            return regex.toString()
        }

    //return "(?:featureSets(UL|DL)-PerCC-r15)?[\\v\\h]*\\[(\\d*)\\](?:[\\w\\d[\\v\\h]-:]*supportedMIMO-Capability(?:DL|UL)(?:-MRDC)?-r15[\\v\\h]:[\\v\\h](\\w*)Layers?)?(?:[\\v\\h]*(?:ul|dl)-(\\w*QAM)-r15 : supported)?";
    override val regexLTEFeatureSetPerCC: String
        get() = "(?:featureSets(UL|DL)-PerCC-r15)?[\\v\\h]*\\[\\d*\\](?:[\\w\\d[\\v\\h]-:]*supportedMIMO-Capability(?:DL|UL)(?:-MRDC)?-r15[\\v\\h]:[\\v\\h](\\w*)Layers?)?(?:[\\v\\h]*(?:ul|dl)-(\\w*QAM)-r15 : supported)?"

    //return "(?:featureSets(UL|DL)-PerCC-r15)?[\\v\\h]*\\[(\\d*)\\](?:[\\w\\d[\\v\\h]-:]*supportedMIMO-Capability(?:DL|UL)(?:-MRDC)?-r15[\\v\\h]:[\\v\\h](\\w*)Layers?)?(?:[\\v\\h]*(?:ul|dl)-(\\w*QAM)-r15 : supported)?";
    override val regexLTEFeatureSetPerCCList: String
        get() {
            val regex = StringBuilder("(?:featureSets(DL|UL)-r15)?[\\v\\h]*\\[(\\d*)\\]")
            val mainRegex =
                "(?:[\\v\\h]*(?:featureSetPerCC-List(?:DL|UL)-r15)?[\\v\\h]*\\[(\\\\d)\\][\\v\\h]:[\\v\\h](\\d*))"
            regex.append(mainRegex.replace("\\\\d", "0"))
            for (i in 1 until ImportCapabilities.lteDlCC) {
                regex.append(mainRegex.replace("\\\\d", i.toString() + "")).append("?")
            }
            return regex.toString()
        }
}