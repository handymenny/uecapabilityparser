package it.smartphonecombo.uecapabilityparser.query

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import it.smartphonecombo.uecapabilityparser.model.band.IBandDetails
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchableField(val name: String, val type: FieldType) {
    companion object {
        fun getAllSearchableFields() = buildList {
            addAll(FieldNumber.entries.map { SearchableField(it.name, FieldType.FieldNumber) })
            addAll(FieldString.entries.map { SearchableField(it.name, FieldType.FieldString) })
            addAll(FieldStrings.entries.map { SearchableField(it.name, FieldType.FieldStrings) })
            addAll(
                FieldBandsDetails.entries.map {
                    SearchableField(it.name, FieldType.FieldBandsDetails)
                }
            )
            addAll(FieldCombos.entries.map { SearchableField(it.name, FieldType.FieldCombos) })
        }
    }
}

enum class FieldType {
    @SerialName("number") FieldNumber,
    @SerialName("string") FieldString,
    @SerialName("strings") FieldStrings,
    @SerialName("bandDetails") FieldBandsDetails,
    @SerialName("combos") FieldCombos
}

enum class FieldNumber {
    LTE_CATEGORY_DL,
    LTE_CATEGORY_UL,
    TIMESTAMP;

    fun extractField(cap: Capabilities): Long {
        return when (this) {
            LTE_CATEGORY_DL -> cap.lteCategoryDL.toLong()
            LTE_CATEGORY_UL -> cap.lteCategoryUL.toLong()
            TIMESTAMP -> cap.timestamp
        }
    }
}

enum class FieldString {
    DESCRIPTION,
    LOG_TYPE;

    fun extractField(cap: Capabilities): String {
        return when (this) {
            DESCRIPTION -> cap.getStringMetadata("description") ?: ""
            LOG_TYPE -> cap.logType.name
        }
    }
}

enum class FieldStrings {
    NSA_BANDS,
    SA_BANDS,
    LTE_ALT_TBS_IND;

    /** Always returns uppercase strings */
    fun extractField(cap: Capabilities): List<String> {
        return when (this) {
            NSA_BANDS -> cap.nrNSAbands.map { it.band.toString() }
            SA_BANDS -> cap.nrSAbands.map { it.band.toString() }
            LTE_ALT_TBS_IND -> cap.altTbsIndexes.map { it.uppercase() }
        }
    }
}

enum class FieldBandsDetails {
    LTE_BANDS,
    NR_BANDS;

    fun extractField(cap: Capabilities): List<IBandDetails> {
        return when (this) {
            LTE_BANDS -> cap.lteBands
            NR_BANDS -> cap.nrBands
        }
    }
}

enum class FieldCombos {
    LTE_COMBOS,
    NR_COMBOS,
    ENDC_COMBOS,
    NRDC_COMBOS;

    fun extractField(cap: Capabilities): List<ICombo> {
        return when (this) {
            LTE_COMBOS -> cap.lteCombos
            NR_COMBOS -> cap.nrCombos
            ENDC_COMBOS -> cap.enDcCombos
            NRDC_COMBOS -> cap.nrDcCombos
        }
    }
}
