package it.smartphonecombo.uecapabilityparser.query

import it.smartphonecombo.uecapabilityparser.model.Capabilities
import kotlinx.serialization.Serializable

@Serializable
data class Query(private val criteriaList: List<Criteria>) {
    /** True if item meets all criteria, False otherwise */
    fun evaluateQuery(item: Capabilities): Boolean = criteriaList.all { it.evaluateCriteria(item) }
}
