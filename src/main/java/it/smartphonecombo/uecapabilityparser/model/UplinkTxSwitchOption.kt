package it.smartphonecombo.uecapabilityparser.model

import kotlinx.serialization.Serializable

@Serializable
enum class UplinkTxSwitchOption {
    NONE,
    SWITCHED_UL,
    DUAL_UL,
    BOTH;

    companion object {
        fun valueOf(name: String?): UplinkTxSwitchOption {
            return when (name) {
                "switchedUL" -> SWITCHED_UL
                "dualUL" -> DUAL_UL
                "both" -> BOTH
                else -> NONE
            }
        }
    }
}

@Serializable
enum class UplinkTxSwitchType {
    R16, // 1Tx-2Tx across 2 carriers
    R17_1T2T, // 1Tx-2Tx across 3 carriers (1 + 2 contiguous)
    R17_2T2T, // 2Tx-2Tx across 3 carriers (1 + 2 contiguous)
    R18_1T, //  1Tx-1Tx, 1Tx-2Tx across 4 carriers
    R18_2T, // 2Tx-2Tx across 4 carriers
}

@Serializable
data class UplinkTxSwitchConfig(val type: UplinkTxSwitchType, val option: UplinkTxSwitchOption)
