package it.smartphonecombo.uecapabilityparser.model

import it.smartphonecombo.uecapabilityparser.model.shannon.lte.ShannonComponentLte
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ShannonComponentLteTest {

    @Test
    fun testBwClass() {
        val classDMimo2 = 0b0001_0000_0000_0000
        val classAMimo1 = 0b1000_0000_0000_0000
        val componentLte = ShannonComponentLte(1, classDMimo2, classAMimo1)

        assertEquals("D".toBwClass(), componentLte.bwClassDl)
        assertEquals("A".toBwClass(), componentLte.bwClassUl)
    }

    @Test
    fun testMimo() {
        val classAMimo4 = 0b0001_0000_0000_0001
        val classAMimo2 = 0b1000_0000_0000_0001
        val componentLte = ShannonComponentLte(1, classAMimo4, classAMimo2)

        assertEquals(4.toMimo(), componentLte.mimoDl)
        assertEquals(2.toMimo(), componentLte.mimoUl)
    }

    @Test
    fun testBwClassNone() {
        val classBMimo2 = 0b0100_0000_0000_0000

        val componentLte = ShannonComponentLte(3, classBMimo2, 0)

        assertEquals("B".toBwClass(), componentLte.bwClassDl)
        assertEquals(BwClass.NONE, componentLte.bwClassUl)
    }
}
