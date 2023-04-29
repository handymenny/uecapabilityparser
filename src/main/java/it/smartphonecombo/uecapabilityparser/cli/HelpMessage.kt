package it.smartphonecombo.uecapabilityparser.cli

object HelpMessage {
    const val INPUT = "UE Capability Parser"
    const val INPUT_NR = "NR UE Capability file"
    const val INPUT_ENDC = "ENDC UE Capability file"
    const val DEFAULT_NR = "Main capability input is NR (otherwise LTE)"
    const val MULTIPLE_0XB826 =
        """Use this option if input contains several 0xB826 hexdumps separated by blank lines and optionally prefixed with "Payload :""""
    const val TYPE =
        """Type of capability. Valid values are: H (UE Capability Hex Dump), 
            W (Wireshark UE Capability Information), N (NSG UE Capability Information), 
            C (Carrier policy), CNR (NR Cap Prune), E (28874 nvitem binary, decompressed), 
            Q (QCAT 0xB0CD), QNR (0xB826 hexdump), M (MEDIATEK CA_COMB_INFO), 
            O (OSIX UE Capability Information), QC (QCAT UE Capability Information)"""
    const val CSV =
        """Output a csv, if "-" is the csv will be output to standard output.
        Some parsers output multiple CSVs, in these cases "-LTE", "-NR", "-EN-DC",
        "-NR-DC" will be added before the extension"""
    const val UE_LOG =
        """Output the uelog, if "-" is specified the uelog will be output to standard output"""
    const val DEBUG = "Print debug info"
    const val JSON_PRETTY_PRINT = "Specifies whether resulting JSONs should be pretty-printed"
    const val JSON =
        """Output a JSON file representing the serialization of capabilities data.
         If "-" is provided in place of a file name, this will json be outputted to standard output."""
    const val SERVER =
        """Starts ue capability parser in server mode, accepting requests to the port passed as value"""
}
