package it.smartphonecombo.uecapabilityparser.model.band

import it.smartphonecombo.uecapabilityparser.extension.Band
import it.smartphonecombo.uecapabilityparser.model.Mimo
import it.smartphonecombo.uecapabilityparser.model.PowerClass
import it.smartphonecombo.uecapabilityparser.model.modulation.Modulation

sealed interface IBandDetails {
    var band: Band
    var mimoDL: Mimo
    var mimoUL: Mimo
    var modDL: Modulation
    var modUL: Modulation
    var powerClass: PowerClass
}
