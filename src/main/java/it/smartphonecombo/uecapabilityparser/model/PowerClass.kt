package it.smartphonecombo.uecapabilityparser.model

import kotlinx.serialization.SerialName

/** Enumeration of Power Classes */
enum class PowerClass {
    @SerialName("none") NONE,
    @SerialName("pc1") PC1,
    @SerialName("pc1dot5") PC1dot5,
    @SerialName("pc2") PC2,
    @SerialName("pc3") PC3,
    @SerialName("pc4") PC4,
    @SerialName("pc5") PC5,
    @SerialName("pc6") PC6,
    @SerialName("pc7") PC7;

    override fun toString(): String {
        return when (this) {
            NONE -> ""
            PC1 -> "1"
            PC1dot5 -> "1.5"
            PC2 -> "2"
            PC3 -> "3"
            PC4 -> "4"
            PC5 -> "5"
            PC6 -> "6"
            PC7 -> "7"
        }
    }
}
