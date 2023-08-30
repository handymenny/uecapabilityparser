package it.smartphonecombo.uecapabilityparser.model.modulation

import kotlinx.serialization.SerialName

enum class ModulationOrder {
    @SerialName("none") NONE,
    @SerialName("qam16") QAM16,
    @SerialName("qam64") QAM64,
    @SerialName("qam256") QAM256,
    @SerialName("qam1024") QAM1024;

    override fun toString(): String {
        return when (this) {
            QAM1024 -> "1024qam"
            QAM256 -> "256qam"
            QAM64 -> "64qam"
            QAM16 -> "16qam"
            NONE -> ""
        }
    }

    companion object {
        /**
         * Return:
         * - [QAM1024] if [string] contains "1024"
         * - [QAM256] if [string] contains "256"
         * - [QAM64] if [string] contains "64"
         * - [QAM16] if [string] contains "16"
         * - [NONE] otherwise
         */
        fun of(string: String?): ModulationOrder {
            return when {
                string == null -> NONE
                "1024" in string -> QAM1024
                "256" in string -> QAM256
                "64" in string -> QAM64
                "16" in string -> QAM16
                else -> NONE
            }
        }
    }
}
