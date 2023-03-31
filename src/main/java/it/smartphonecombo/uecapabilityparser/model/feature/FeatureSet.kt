package it.smartphonecombo.uecapabilityparser.model.feature

import it.smartphonecombo.uecapabilityparser.model.LinkDirection

data class FeatureSet(val featureSetsPerCC: List<IFeaturePerCC>, val type: LinkDirection)
