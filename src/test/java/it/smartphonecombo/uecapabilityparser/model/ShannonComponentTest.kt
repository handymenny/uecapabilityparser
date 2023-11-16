package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.shannon.ShannonComponent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ShannonComponentTest {

    @Test
    fun testIsNrTrue() {
        val componentNr = ShannonComponent(10048, 0, 0, 0, 0)
        assertTrue(componentNr.isNr)
    }

    @Test
    fun testIsNrFalse() {
        val componentLte = ShannonComponent(48, 0, 0, 0, 0)
        assertFalse(componentLte.isNr)
    }

    @Test
    fun testConvertBandLTE() {
        val componentLte = ShannonComponent(3, 0, 0, 0, 0)
        assertEquals(3, componentLte.band)
    }

    @Test
    fun testConvertBandNr() {
        val componentNr = ShannonComponent(10257, 0, 0, 0, 0)
        assertEquals(257, componentNr.band)
    }

    @Test
    fun testBwClass() {
        val componentLte = ShannonComponent(3, 3, 1, 0, 0, List(2) { 0 }, List(2) { 0 })

        assertEquals(BwClass('C'), componentLte.bwClassDl)
        assertEquals(BwClass('A'), componentLte.bwClassUl)
    }

    @Test
    fun testBwClassNone() {
        val componentLte = ShannonComponent(3, 2, 0, 0, 0, List(2) { 0 })

        assertEquals(BwClass('B'), componentLte.bwClassDl)
        assertEquals(BwClass.NONE, componentLte.bwClassUl)
    }

    @Test
    fun testBwClassMmWave() {
        val componentLte = ShannonComponent(10257, 13, 1, 0, 0, List(8) { 0 }, List(8) { 0 })

        assertEquals(BwClass('M'), componentLte.bwClassDl)
        assertEquals(BwClass('A'), componentLte.bwClassUl)
    }
}
