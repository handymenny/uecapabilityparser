package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.shannon.ComboFeatures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ComboFeaturesTest {

    @Test
    fun testBcsAllNull() {
        val input = null
        val oracle = EmptyBCS
        checkBcs(input, input, input, oracle, oracle, oracle)
    }

    @Test
    fun testBcsOneNull() {
        val input = 0L
        val inputIntraEnDc = null
        val oracle = SingleBCS(0)
        val oracleIntraEnDc = EmptyBCS
        checkBcs(input, inputIntraEnDc, input, oracle, oracleIntraEnDc, oracle)
    }

    @Test
    fun testBcsAllZero() {
        val input = 0L
        val oracle = SingleBCS(0)
        checkBcs(input, input, input, oracle, oracle, oracle)
    }

    @Test
    fun testBcsAllZero2() {
        val input = 0b10000000000000000000000000000000L
        val oracle = SingleBCS(0)
        checkBcs(input, input, input, oracle, oracle, oracle)
    }

    @Test
    fun testBcsOneZero() {
        val input = null
        val inputEutra = 0L
        val oracle = EmptyBCS
        val oracleEutra = SingleBCS(0)
        checkBcs(input, input, inputEutra, oracle, oracle, oracleEutra)
    }

    @Test
    fun testBcsSingle() {
        val input = 0b00100000000000000000000000000000L
        val oracle = SingleBCS(2)
        checkBcs(input, input, input, oracle, oracle, oracle)
    }

    @Test
    fun testBcsMulti() {
        val input = 0b11101000000000000000000000000000L
        val oracle = MultiBCS(intArrayOf(0, 1, 2, 4))
        checkBcs(input, input, input, oracle, oracle, oracle)
    }

    @Test
    fun testBcsMultiAllDifferent() {
        val inputNr = 0b11000000000000000000000000000000L
        val inputIntraEndc = 0b11100000000000000000000000000000L
        val inputEutra = 0b11111000000000000000000000000000L
        val oracleNr = MultiBCS(intArrayOf(0, 1))
        val oracleIntraEndc = MultiBCS(intArrayOf(0, 1, 2))
        val oracleEutra = MultiBCS(intArrayOf(0, 1, 2, 3, 4))
        checkBcs(inputNr, inputIntraEndc, inputEutra, oracleNr, oracleIntraEndc, oracleEutra)
    }

    @Test
    fun testBcsAll() {
        val input = 0xFFFFFFFF
        val oracle = AllBCS
        checkBcs(input, input, input, oracle, oracle, oracle)
    }

    private fun checkBcs(
        bcsNr: Long?,
        bcsIntraEndc: Long?,
        bcsEutra: Long?,
        oracleBcsNr: BCS,
        oracleBcsIntraEndc: BCS,
        oracleBcsEutra: BCS
    ) {
        val combo = ComboFeatures(bcsNr, bcsIntraEndc, bcsEutra, null, null)
        assertEquals(oracleBcsNr, combo.bcsNr)
        assertEquals(oracleBcsIntraEndc, combo.bcsIntraEndc)
        assertEquals(oracleBcsEutra, combo.bcsEutra)
    }
}
