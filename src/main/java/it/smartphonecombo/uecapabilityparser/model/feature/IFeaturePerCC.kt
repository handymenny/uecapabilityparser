package it.smartphonecombo.uecapabilityparser.model.feature

import it.smartphonecombo.uecapabilityparser.extension.Mimo
import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.Modulation

sealed interface IFeaturePerCC {
    val type: LinkDirection
    val mimo: Mimo
    val qam: Modulation
}
