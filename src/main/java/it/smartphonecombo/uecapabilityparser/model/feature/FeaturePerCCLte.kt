package it.smartphonecombo.uecapabilityparser.model.feature

import it.smartphonecombo.uecapabilityparser.model.EmptyMimo
import it.smartphonecombo.uecapabilityparser.model.LinkDirection
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.Modulation

data class FeaturePerCCLte(
    override val type: LinkDirection = LinkDirection.DOWNLINK,
    override val mimo: Mimo = EmptyMimo,
    override val qam: Modulation = Modulation.NONE
) : IFeaturePerCC
