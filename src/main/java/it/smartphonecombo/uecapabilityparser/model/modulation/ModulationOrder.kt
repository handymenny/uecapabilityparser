package it.smartphonecombo.uecapabilityparser.model.modulation

enum class ModulationOrder {
    NONE,
    QPSK,
    QAM16,
    QAM64,
    QAM256,
    QAM1024;

    override fun toString(): String {
        return when (this) {
            QAM1024 -> "1024qam"
            QAM256 -> "256qam"
            QAM64 -> "64qam"
            QAM16 -> "16qam"
            QPSK -> "qpsk"
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
