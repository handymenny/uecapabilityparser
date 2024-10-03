package it.smartphonecombo.uecapabilityparser.query

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.BwClass
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr
import it.smartphonecombo.uecapabilityparser.model.component.IComponent
import it.smartphonecombo.uecapabilityparser.model.toBwClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface IComponentValue {
    fun matches(component: IComponent): Boolean

    fun allIndexes(list: List<IComponent>): List<Int> {
        return list.mapIndexedNotNull { index, it -> if (matches(it)) index else null }
    }
}

@Serializable
@SerialName("lteDl")
data class LteComponentDlValue(
    val band: Band,
    val minBwClass: BwClass = "A".toBwClass(),
    val minMimo: Int = 0,
) : IComponentValue {
    override fun matches(component: IComponent): Boolean {
        return component.band == band &&
            component.classDL >= minBwClass &&
            component.mimoDL.max() >= minMimo
    }
}

@Serializable
@SerialName("lteUl")
data class LteComponentUlValue(val band: Band, val minBwClass: BwClass = "A".toBwClass()) :
    IComponentValue {
    override fun matches(component: IComponent): Boolean {
        return component.band == band && component.classUL >= minBwClass
    }
}

@Serializable
@SerialName("nrDl")
data class NrComponentDlValue(
    val band: Band,
    val minBwClass: BwClass = "A".toBwClass(),
    val minMimo: Int = 0,
    val minMaxBwPerCC: Int = 0,
) : IComponentValue {
    override fun matches(component: IComponent): Boolean {
        if (component !is ComponentNr) return false

        return component.band == band &&
            component.classDL >= minBwClass &&
            component.mimoDL.max() >= minMimo &&
            component.maxBandwidthDl.max() >= minMaxBwPerCC
    }
}

@Serializable
@SerialName("nrUl")
data class NrComponentUlValue(
    val band: Band,
    val minBwClass: BwClass = "A".toBwClass(),
    val minMimo: Int = 0,
    val minMaxBwPerCC: Int = 0,
) : IComponentValue {
    override fun matches(component: IComponent): Boolean {
        if (component !is ComponentNr) return false

        return component.band == band &&
            component.classUL >= minBwClass &&
            component.mimoUL.max() >= minMimo &&
            component.maxBandwidthUl.max() >= minMaxBwPerCC
    }
}
