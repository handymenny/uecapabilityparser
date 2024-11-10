package it.smartphonecombo.uecapabilityparser.extension

import it.smartphonecombo.uecapabilityparser.model.Duplex
import it.smartphonecombo.uecapabilityparser.model.band.DuplexBandTable
import it.smartphonecombo.uecapabilityparser.model.component.ComponentNr

val ComponentNr.isSUL
    get() = DuplexBandTable.getNrDuplex(this.band) == Duplex.SUL
