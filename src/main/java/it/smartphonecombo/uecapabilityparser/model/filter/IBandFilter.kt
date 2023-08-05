package it.smartphonecombo.uecapabilityparser.model.filter

import it.smartphonecombo.uecapabilityparser.extension.Band

sealed interface IBandFilter {
    var band: Band
}
