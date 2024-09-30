package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.shannon.nr.ShannonComponentNr
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ShannonComponentNrTest {

    @Test
    fun testIsNrTrue() {
        val componentNr = ShannonComponentNr(10048, 0, 0, 0, 0)
        assertTrue(componentNr.isNr)
    }

    @Test
    fun testIsNrFalse() {
        val componentLte = ShannonComponentNr(48, 0, 0, 0, 0)
        assertFalse(componentLte.isNr)
    }

    @Test
    fun testConvertBandLTE() {
        val componentLte = ShannonComponentNr(3, 0, 0, 0, 0)
        assertEquals(3, componentLte.band)
    }

    @Test
    fun testConvertBandNr() {
        val componentNr = ShannonComponentNr(10257, 0, 0, 0, 0)
        assertEquals(257, componentNr.band)
    }

    @Test
    fun testBwClass() {
        val componentLte = ShannonComponentNr(3, 3, 1, 0, 0, List(2) { 0 }, List(2) { 0 })

        assertEquals("C".toBwClass(), componentLte.bwClassDl)
        assertEquals("A".toBwClass(), componentLte.bwClassUl)
    }

    @Test
    fun testBwClassNone() {
        val componentLte = ShannonComponentNr(3, 2, 0, 0, 0, List(2) { 0 })

        assertEquals("B".toBwClass(), componentLte.bwClassDl)
        assertEquals(BwClass.NONE, componentLte.bwClassUl)
    }

    @Test
    fun testBwClassMmWave() {
        val componentLte = ShannonComponentNr(10257, 13, 1, 0, 0, List(8) { 0 }, List(8) { 0 })

        assertEquals("M".toBwClass(), componentLte.bwClassDl)
        assertEquals("A".toBwClass(), componentLte.bwClassUl)
    }
}
