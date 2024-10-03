package it.smartphonecombo.uecapabilityparser.query

import it.smartphonecombo.uecapabilityparser.io.IOUtils.echoSafe
import it.smartphonecombo.uecapabilityparser.model.combo.ICombo
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface IComboValue {
    fun matches(combo: ICombo): Boolean

    fun matchesAny(list: List<ICombo>): Boolean = list.any { matches(it) }
}

@Serializable
@SerialName("simple")
data class ComboValue(
    @SerialName("dl") val dlComponents: List<IComponentValue>,
    @SerialName("ul") val ulComponents: List<IComponentValue>,
) : IComboValue {
    override fun matches(combo: ICombo): Boolean {
        val components = combo.masterComponents
        val res = componentsCheck(components, dlComponents, ulComponents)
        if (res) echoSafe("Matched: " + combo.toCompactStr())
        return res
    }
}

@Serializable
@SerialName("mrdc")
data class ComboMrDcValue(
    @SerialName("dlMaster") val dlMasterComponents: List<IComponentValue>,
    @SerialName("ulMaster") val ulMasterComponents: List<IComponentValue>,
    @SerialName("dlSecondary") val dlSecondaryComponents: List<IComponentValue>,
    @SerialName("ulSecondary") val ulSecondaryComponents: List<IComponentValue>,
) : IComboValue {
    override fun matches(combo: ICombo): Boolean {
        val masterComponents = combo.masterComponents
        val secondaryComponents = combo.secondaryComponents
        val res =
            componentsCheck(masterComponents, dlMasterComponents, ulMasterComponents) &&
                componentsCheck(secondaryComponents, dlSecondaryComponents, ulSecondaryComponents)

        if (res) echoSafe("Matched: " + combo.toCompactStr())
        return res
    }
}

internal fun componentsCheck(
    components: List<IComponent>,
    dlCriteria: List<IComponentValue>,
    ulCriteria: List<IComponentValue>,
): Boolean {
    return if (components.size < ulCriteria.size || components.size < dlCriteria.size) {
        false
    } else {
        meetsCriteriaReduced(ulCriteria, components) && meetsCriteriaReduced(dlCriteria, components)
    }
}

internal fun meetsCriteriaReduced(
    criteria: List<IComponentValue>,
    components: List<IComponent>,
): Boolean {
    val indexes =
        criteria.map {
            val res = it.allIndexes(components)
            if (res.isEmpty()) return false
            res
        }

    return reducedResult(indexes)
}

// Return true if all sublist have at least one unique value.
// To maximize lists that have unique values, this function eliminates duplicates (values that
// appears in
// multiple sub-lists) using a greedy-like algorithm.
internal fun reducedResult(list: List<List<Int>>): Boolean {
    val allValues = list.flatten()
    val distinctValues = allValues.distinct()

    // Simple case
    val simpleCase =
        when {
            // Empty input
            list.isEmpty() -> true
            // At least one sublist is empty
            list.any { it.isEmpty() } -> false
            // No duplicate case
            allValues.size == distinctValues.size -> distinctValues.size >= list.size
            // Not enough distinct values case
            distinctValues.size < list.size -> false
            else -> null
        }

    if (simpleCase != null) return simpleCase

    // - General -- expensive -- case

    // Add a weight to each value. weight = number of sub-lists containing that value
    val valueWeights = distinctValues.associateWith { index -> list.count { it.contains(index) } }
    // List sorted by sum of all weights
    val sortedList = list.sortedBy { subList -> subList.sumOf { value -> valueWeights[value]!! } }

    // store values already taken
    val valuesTaken = mutableSetOf<Int>()

    for (subList in sortedList) {
        val int = subList.subtract(valuesTaken).minByOrNull { valueWeights[it]!! }
        // found an empty sublist
        if (int == null) break
        valuesTaken.add(int)
    }

    return valuesTaken.size == list.size
}
