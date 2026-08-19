package `in`.dharmaposhanam.lekhani

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Sanskrit layout is the only flavor that offers svara marks, so these
 * assertions live in the Sanskrit source set.
 *
 * Marks are written as \u escapes: they are combining and Private Use Area
 * characters that render as dotted circles or blanks, and pasting them as
 * literals is how they get silently lost.
 */
class SanskritSvaraPopupTest {

    private val svaritaUnicode = "\u0951"
    private val anudattaUnicode = "\u0952"
    private val deerghaSvaritaUnicode = "\u1CDA"
    private val svaritaPua = "\uE302"
    private val anudattaPua = "\uE301"
    private val deerghaSvaritaPua = "\uE303"

    private val anusvara = "\u0902"
    private val chandrabindu = "\u0901"
    private val visarga = "\u0903"

    private val unicodeMarks = listOf(svaritaUnicode, anudattaUnicode, deerghaSvaritaUnicode)
    private val puaMarks = listOf(svaritaPua, anudattaPua, deerghaSvaritaPua)

    @After
    fun resetEncoding() {
        SvaraMarks.setEncoding(SvaraEncoding.DEFAULT)
    }

    @Test
    fun `anusvara offers the unicode svara marks`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        assertTrue(PopupMappings.getAlternates(anusvara)!!.containsAll(unicodeMarks))
    }

    @Test
    fun `anusvara offers the PUA svara marks in vijayadv mode`() {
        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)

        val alternates = PopupMappings.getAlternates(anusvara)!!

        assertTrue(alternates.containsAll(puaMarks))
        // The two encodings must not mix in one popup
        assertFalse(alternates.contains(deerghaSvaritaUnicode))
        assertFalse(alternates.contains(svaritaUnicode))
    }

    @Test
    fun `chandrabindu offers svara marks too`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        assertTrue(PopupMappings.getAlternates(chandrabindu)!!.containsAll(unicodeMarks))
    }

    @Test
    fun `svara host keys keep their non-svara alternates`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        assertTrue(
            PopupMappings.getAlternates(anusvara)!!.containsAll(listOf(chandrabindu, visarga))
        )
    }

    @Test
    fun `a svara key offers the other two marks and not itself`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        assertEquals(
            listOf(anudattaUnicode, deerghaSvaritaUnicode),
            PopupMappings.getAlternates(svaritaUnicode)
        )
    }

    @Test
    fun `switching encoding changes what the popup would commit`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)
        val unicodeAlternates = PopupMappings.getAlternates(anusvara)!!

        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)
        val puaAlternates = PopupMappings.getAlternates(anusvara)!!

        assertFalse(unicodeAlternates == puaAlternates)
    }

    @Test
    fun `unmapped keys still return null`() {
        assertNull(PopupMappings.getAlternates("क्ष्म"))
        assertFalse(PopupMappings.hasAlternates("क्ष्म"))
    }

    @Test
    fun `consonant alternates are unaffected by encoding`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)
        val unicodeAlternates = PopupMappings.getAlternates("क")

        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)

        assertEquals(unicodeAlternates, PopupMappings.getAlternates("क"))
    }

    @Test
    fun `both pages place the settings gear beside the spacebar`() {
        listOf(0, 1).forEach { pageIndex ->
            assertEquals(
                listOf(
                    SpecialKey.SETTINGS.display,
                    SpecialKey.SPACE.display,
                    SpecialKey.RETURN.display
                ),
                KeyboardLayouts.getPage(pageIndex).rows.last()
            )
        }
    }

    @Test
    fun `svara keys are identified in either encoding`() {
        assertTrue(KeyboardLayouts.isSvaraKey(svaritaUnicode))
        assertTrue(KeyboardLayouts.isSvaraKey(deerghaSvaritaPua))
        assertFalse(KeyboardLayouts.isSvaraKey("क"))
    }

    @Test
    fun `svara keys display on a base vowel`() {
        assertEquals("अ" + deerghaSvaritaUnicode, KeyboardLayouts.getSvaraDisplayText(deerghaSvaritaUnicode))
    }
}
