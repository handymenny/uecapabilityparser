package it.smartphonecombo.uecapabilityparser.importer

import it.smartphonecombo.uecapabilityparser.importer.nr.ImportCapPrune
import org.junit.jupiter.api.Test

internal class ImportCapPruneTest {
    @Test
    fun parse() {
        val cap =
            "b2A[4]A[1]-b2A[4]-b30A[4]-b66A[4]-n5A[2]A[1];b2A[4]-b2A[4]-b30A[4]A[1]-b66A[4]-n5A[2]A[1];b2A[4]-b2A[4]-b30A[4]-b66A[4]A[1]-n5A[2]A[1];b2A[4]A[1]-b30A[4]-b66A[4]-b66A[4]-n5A[2]A[1];b2A[4]-b30A[4]A[1]-b66A[4]-b66A[4]-n5A[2]A[1];b2A[4]-b30A[4]-b66A[4]A[1]-b66A[4]-n5A[2]A[1];b2A[4]A[1]-b2A[4]-b66A[4]-b66A[4]-n5A[2]A[1];b2A[4]-b2A[4]-b66A[4]A[1]-b66A[4]-n5A[2]A[1];b2A[4]A[1]-b46D[2,2,2]-n5A[2]A[1];b2A[4]A[1]-b2A[4]-n260I[2,2,2,2]A[2];b2A[4]A[1]-b5A[2]-n260I[2,2,2,2]A[2];b2A[4]-b5A[2]A[1]-n260I[2,2,2,2]A[2];b2A[4]A[1]-b12A[2]-n260I[2,2,2,2]A[2];b2A[4]-b12A[2]A[1]-n260I[2,2,2,2]A[2];b2A[4]A[1]-b29A[2]-n260I[2,2,2,2]A[2];b2A[4]A[1]-b30A[4]-n260I[2,2,2,2]A[2];b2A[4]-b30A[4]A[1]-n260I[2,2,2,2]A[2];b2A[4]A[1]-b66A[4]-n260I[2,2,2,2]A[2];b2A[4]-b66A[4]A[1]-n260I[2,2,2,2]A[2];b5A[2]A[1]-b30A[4]-n260I[2,2,2,2]A[2];b5A[2]-b30A[4]A[1]-n260I[2,2,2,2]A[2];b5A[2]A[1]-b66A[4]-n260I[2,2,2,2]A[2];b5A[2]-b66A[4]A[1]-n260I[2,2,2,2]A[2];b12A[2]A[1]-b30A[4]-n260I[2,2,2,2]A[2];b12A[2]-b30A[4]A[1]-n260I[2,2,2,2]A[2];b12A[2]-b66A[4]A[1]-n260I[2,2,2,2]A[2];b30A[4]A[1]-b66A[4]-n260I[2,2,2,2]A[2];b30A[4]-b66A[4]A[1]-n260I[2,2,2,2]A[2];b66A[4]A[1]-b66A[4]-n260I[2,2,2,2]A[2];"
        val output: String = ImportCapPrune().parse(cap).enDcCombos.toString()
        println(output)
    }

    @Test
    fun parse2() {
        val cap =
            "b2AA-b46D-b66A-n71AA;b2A-b46D-b66AA-n71AA;b2AA-b66C-b71A-n71AA;b2A-b66CA-b71A-n71AA;b2AA-b66A-n41AA;b2A-b66AA-n41AA;b2AA-b66A-n260AA-n260A;b2A-b66AA-n260AA-n260A;b2AA-b66A-n261AA-n261A;b2A-b66AA-n261AA-n261A;b66AA-n260AA-n260A-n260A-n260A;b66AA-n261AA-n261A-n261A-n261A;b2AA-n260AA-n260A-n260A-n260A;b2AA-n261AA-n261A-n261A-n261A"
        val output: String = ImportCapPrune().parse(cap).enDcCombos.toString()
        println(output)
    }
}
