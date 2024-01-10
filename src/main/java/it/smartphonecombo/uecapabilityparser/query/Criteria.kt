package it.smartphonecombo.uecapabilityparser.query

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Criteria {
    /** Field to compare */
    val field: Any
    /** Operator to use for comparison */
    val comparator: Comparator
    /** Argument of comparison, some comparator don't need it */
    val value: Any?

    /** True if item meets criteria, False otherwise */
    fun evaluateCriteria(item: Capabilities): Boolean
}

@Serializable
@SerialName("number")
data class CriteriaNumber(
    override val field: FieldNumber,
    override val comparator: Comparator,
    override val value: Long
) : Criteria {
    override fun evaluateCriteria(item: Capabilities): Boolean {
        val field = field.extractField(item)
        if (field == 0L) return false

        return when (comparator) {
            Comparator.EQUALS -> field == value
            Comparator.GREATER -> field > value
            Comparator.LESS -> field < value
            Comparator.NOT_EQUALS -> field != value
            else -> throw IllegalArgumentException()
        }
    }
}

@Serializable
@SerialName("string")
data class CriteriaString(
    override val field: FieldString,
    override val comparator: Comparator,
    override val value: String
) : Criteria {
    override fun evaluateCriteria(item: Capabilities): Boolean {
        val field = field.extractField(item)
        if (field.isEmpty()) return false

        return when (comparator) {
            Comparator.EQUALS -> field.equals(value, ignoreCase = true)
            Comparator.NOT_EQUALS -> !field.equals(value, ignoreCase = true)
            Comparator.CONTAINS -> field.contains(value, ignoreCase = true)
            Comparator.NOT_CONTAINS -> !field.contains(value, ignoreCase = true)
            else -> throw IllegalArgumentException()
        }
    }
}

@Serializable
@SerialName("strings")
data class CriteriaStrings(
    override val field: FieldStrings,
    override val comparator: Comparator,
    override val value: List<String>? = null
) : Criteria {
    private val valueUpperCase
        get() = value?.map { it.uppercase() } ?: emptyList()

    override fun evaluateCriteria(item: Capabilities): Boolean {
        val field = field.extractField(item)
        if (field.isEmpty()) {
            return when (comparator) {
                Comparator.IS_EMPTY -> true
                Comparator.IS_NOT_EMPTY -> false
                else -> false
            }
        }

        return when (comparator) {
            Comparator.HAS_ANY -> valueUpperCase.any { field.contains(it) }
            Comparator.HAS_ALL -> valueUpperCase.let { field.containsAll(it) }
            Comparator.HAS_NONE -> valueUpperCase.none { field.contains(it) }
            Comparator.IS_EMPTY -> field.isEmpty()
            Comparator.IS_NOT_EMPTY -> field.isNotEmpty()
            else -> throw IllegalArgumentException()
        }
    }
}

@Serializable
@SerialName("bands")
data class CriteriaBands(
    override val field: FieldBandsDetails,
    override val comparator: Comparator,
    override val value: List<IBandDetailsValue>? = null
) : Criteria {

    override fun evaluateCriteria(item: Capabilities): Boolean {
        val field = field.extractField(item)
        if (field.isEmpty()) {
            return when (comparator) {
                Comparator.IS_EMPTY -> true
                Comparator.IS_NOT_EMPTY -> false
                else -> false
            }
        }

        return when (comparator) {
            Comparator.HAS_ANY -> value!!.any { it.matchesAny(field) }
            Comparator.HAS_ALL -> value!!.all { it.matchesAny(field) }
            Comparator.HAS_NONE -> value!!.none { it.matchesAny(field) }
            Comparator.IS_EMPTY -> field.isEmpty()
            Comparator.IS_NOT_EMPTY -> field.isNotEmpty()
            else -> throw IllegalArgumentException()
        }
    }
}

@Serializable
@SerialName("combos")
data class CriteriaCombos(
    override val field: FieldCombos,
    override val comparator: Comparator,
    override val value: List<IComboValue>? = null
) : Criteria {

    override fun evaluateCriteria(item: Capabilities): Boolean {
        val fieldList = field.extractField(item)
        if (fieldList.isEmpty()) {
            return when (comparator) {
                Comparator.IS_EMPTY -> true
                Comparator.IS_NOT_EMPTY -> false
                else -> false
            }
        }

        return when (comparator) {
            Comparator.HAS_ANY -> value!!.any { criteria -> criteria.matchesAny(fieldList) }
            Comparator.HAS_ALL -> value!!.all { criteria -> criteria.matchesAny(fieldList) }
            Comparator.HAS_NONE -> value!!.none { criteria -> criteria.matchesAny(fieldList) }
            Comparator.IS_EMPTY -> fieldList.isEmpty()
            Comparator.IS_NOT_EMPTY -> fieldList.isNotEmpty()
            else -> throw IllegalArgumentException()
        }
    }
}
