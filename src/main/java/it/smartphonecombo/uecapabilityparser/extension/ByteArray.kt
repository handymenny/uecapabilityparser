package it.smartphonecombo.uecapabilityparser.extension

internal fun ByteArray.isLteUeCapInfoPayload() = isNotEmpty() && this[0] in 0x38..0x3E

internal fun ByteArray.isNrUeCapInfoPayload() = isNotEmpty() && this[0] in 0x48..0x4E
