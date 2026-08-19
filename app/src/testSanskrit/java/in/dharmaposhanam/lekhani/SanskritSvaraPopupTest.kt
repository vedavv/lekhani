package `in`.dharmaposhanam.lekhani

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Sanskrit layout is the only flavor that offers svara marks, so these
 * assertions live in the Sanskrit source set.
 */
class SanskritSvaraPopupTest {

    @After
    fun resetEncoding() {
        SvaraMarks.setEncoding(SvaraEncoding.DEFAULT)
    }

    @Test
    fun `anusvara offers the unicode svara marks`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        val alternates = PopupMappings.getAlternates("ं")!!

        assertTrue(alternates.containsAll(listOf("॑", "॒", "᳚")))
    }

    @Test
    fun `anusvara offers the PUA svara marks in vijayadv mode`() {
        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)

        val alternates = PopupMappings.getAlternates("ं")!!

        assertTrue(alternates.containsAll(listOf("", "", "")))
        assertFalse(alternates.contains("᳚"))
    }

    @Test
    fun `chandrabindu offers svara marks too`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        assertTrue(PopupMappings.getAlternates("ँ")!!.containsAll(listOf("॑", "॒", "᳚")))
    }

    @Test
    fun `svara host keys keep their non-svara alternates`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        val alternates = PopupMappings.getAlternates("ं")!!

        assertTrue(alternates.containsAll(listOf("ँ", "ः")))
    }

    @Test
    fun `a svara key offers the other two marks and not itself`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        val alternates = PopupMappings.getAlternates("॑")!!

        assertEquals(listOf("॒", "᳚"), alternates)
    }

    @Test
    fun `switching encoding changes what the popup would commit`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)
        val unicodeAlternates = PopupMappings.getAlternates("ं")!!

        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)
        val puaAlternates = PopupMappings.getAlternates("ं")!!

        assertFalse(unicodeAlternates == puaAlternates)
    }

    @Test
    fun `unmapped keys still return null`() {
        assertEquals(null, PopupMappings.getAlternates("क्ष्म"))
        assertFalse(PopupMappings.hasAlternates("क्ष्म"))
    }

    @Test
    fun `consonant alternates are unaffected by encoding`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)
        val unicodeAlternates = PopupMappings.getAlternates("क")

        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)
        val puaAlternates = PopupMappings.getAlternates("क")

        assertEquals(unicodeAlternates, puaAlternates)
    }

    @Test
    fun `both pages place the settings gear beside the spacebar`() {
        listOf(0, 1).forEach { pageIndex ->
            val bottomRow = KeyboardLayouts.getPage(pageIndex).rows.last()

            assertEquals(
                listOf(
                    SpecialKey.SETTINGS.display,
                    SpecialKey.SPACE.display,
                    SpecialKey.RETURN.display
                ),
                bottomRow
            )
        }
    }

    @Test
    fun `svara keys are identified in either encoding`() {
        assertTrue(KeyboardLayouts.isSvaraKey("॑"))
        assertTrue(KeyboardLayouts.isSvaraKey(""))
        assertFalse(KeyboardLayouts.isSvaraKey("क"))
    }

    @Test
    fun `svara keys display on a base vowel`() {
        assertEquals("अ᳚", KeyboardLayouts.getSvaraDisplayText("᳚"))
    }
}
