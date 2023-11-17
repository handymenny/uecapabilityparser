package it.smartphonecombo.uecapabilityparser.cli

object HelpMessage {
    const val INPUT =
        "A list of capability sources separated by comma. " +
            "These inputs combined must represent a single capability source, if you want to parse different capabilities you can provide multiple --input"
    const val TYPE =
        """Type of capability. Valid values are: H (UE Capability Hex Dump), 
            W (Wireshark UE Capability Information), N (NSG UE Capability Information), 
            C (Carrier policy), CNR (NR Cap Prune), E (28874 nvitem binary), 
            Q (QCAT 0xB0CD), QLTE (0xB0CD hexdump), QNR (0xB826 hexdump), M (MEDIATEK CA_COMB_INFO), 
            O (OSIX UE Capability Information), QC (QCAT UE Capability Information),
            RF (QCT Modem Capabilities), SHNR (Shannon NR UE Cap Config Protobuf)"""
    const val SUBTYPES =
        """A list of subtypes separated by comma, one for each capability source, applicable only to --type H.
            Valid values are: LTE (rat-type EUTRA or LTE UE capability information),
             NR (rat-type NR or NR UE capability information),
             ENDC (rat-type EUTRA-NR)"""
    const val CSV =
        """Output a csv, if "-" is the csv will be output to standard output.
        Some parsers output multiple CSVs, in these cases "-LTECA", "-NRCA", "-ENDC",
        "-NRDC" will be added before the extension"""
    const val UE_LOG =
        """Output the uelog, if "-" is specified the uelog will be output to standard output"""
    const val DEBUG = "Print debug info"
    const val JSON_PRETTY_PRINT = "Specifies whether resulting JSONs should be pretty-printed"
    const val JSON =
        """Output a JSON file representing the serialization of capabilities data.
         If "-" is provided in place of a file name, this will json be outputted to standard output.
         For multiple --input an array is returned."""
    const val PORT = """Listen to the port passed as value, for value 0 a random port is used"""
    const val STORE = "Store the capabilities in the given directory for further retrieval"
    const val COMPRESSION =
        "Compress new stored capabilities, it doesn't affect the ability to read already compressed capabilities"
    const val REPARSE =
        """Re-parse stored capabilities with the given strategy. 
            Valid values are "off" (feature disabled), 
            "force" (re-parse all),
            "auto" (re-parse when parser version differs).
            A backup of the re-parsed data is kept in the backup folder.
            Note: --compression affects the capabilities re-parsed."""
    const val ERROR_TYPE_INPUT_MISMATCH = "A --type must be provided for each --input."
    const val ERROR_SUBTYPES_TYPE_MISMATCH =
        "A --subTypes must be provided for each --type H, it shouldn't be provided for other types."
    const val ERROR_SUBTYPES_INPUT_MISMATCH =
        "Each --subTypes must provide the same quantity of items as the corresponding --input."
    const val ERROR_SUBTYPES_DUPLICATE =
        "A subtype cannot appear multiple times in the same --subTypes."
    const val ERROR_MULTIPLE_INPUTS_UNSUPPORTED =
        "Type E and SHNR don't support multiple inputs in one --input option. Use multiple --input instead."
}
