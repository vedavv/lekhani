package `in`.dharmaposhanam.lekhani

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Codepoints are written as \u escapes throughout. These characters are
 * combining marks and Private Use Area glyphs that render as dotted circles or
 * blanks in most editors, so the escaped form is the only readable one - and it
 * survives copying between tools intact.
 */
class SvaraMarksTest {

    private val svaritaUnicode = '\u0951'
    private val anudattaUnicode = '\u0952'
    private val deerghaSvaritaUnicode = '\u1CDA'
    private val svaritaPua = '\uE302'
    private val anudattaPua = '\uE301'
    private val deerghaSvaritaPua = '\uE303'
    private val baseVowel = '\u0905'  // अ

    @After
    fun resetEncoding() {
        SvaraMarks.setEncoding(SvaraEncoding.DEFAULT)
    }

    @Test
    fun `defaults to unicode encoding`() {
        assertEquals(SvaraEncoding.UNICODE, SvaraEncoding.DEFAULT)
        assertEquals(SvaraEncoding.UNICODE, SvaraMarks.encoding)
    }

    @Test
    fun `unicode encoding emits the standard codepoints`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        assertEquals(0x0951, SvaraMarks.svarita.code)
        assertEquals(0x0952, SvaraMarks.anudatta.code)
        assertEquals(0x1CDA, SvaraMarks.deerghaSvarita.code)
    }

    @Test
    fun `vijayadv encoding emits the private use area codepoints`() {
        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)

        assertEquals(0xE302, SvaraMarks.svarita.code)
        assertEquals(0xE301, SvaraMarks.anudatta.code)
        assertEquals(0xE303, SvaraMarks.deerghaSvarita.code)
    }

    @Test
    fun `marks follow the active encoding`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)
        assertEquals(
            listOf("$svaritaUnicode", "$anudattaUnicode", "$deerghaSvaritaUnicode"),
            SvaraMarks.marks()
        )

        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)
        assertEquals(
            listOf("$svaritaPua", "$anudattaPua", "$deerghaSvaritaPua"),
            SvaraMarks.marks()
        )
    }

    @Test
    fun `svara marks are recognised in either encoding`() {
        listOf(
            svaritaUnicode, anudattaUnicode, deerghaSvaritaUnicode,
            svaritaPua, anudattaPua, deerghaSvaritaPua
        ).forEach {
            assertTrue("U+%04X should be a svara mark".format(it.code), SvaraMarks.isSvaraMark(it))
        }
    }

    @Test
    fun `ordinary letters and anusvara are not svara marks`() {
        listOf(baseVowel, 'a', 'ं').forEach {
            assertFalse("U+%04X should not be a svara mark".format(it.code), SvaraMarks.isSvaraMark(it))
        }
    }

    @Test
    fun `display text puts the mark on a base vowel`() {
        assertEquals("$baseVowel$svaritaUnicode", SvaraMarks.getDisplayText(svaritaUnicode))
        assertEquals("$baseVowel$deerghaSvaritaUnicode", SvaraMarks.getDisplayText(deerghaSvaritaUnicode))
    }

    @Test
    fun `PUA marks also display on a base vowel`() {
        assertEquals("$baseVowel$deerghaSvaritaPua", SvaraMarks.getDisplayText(deerghaSvaritaPua))
        assertEquals("$baseVowel$svaritaPua", SvaraMarks.getDisplayText(svaritaPua))
    }

    @Test
    fun `matras are left alone so other flavors are unaffected`() {
        // Only svara marks get a base vowel. Matras are shared with the Hindi,
        // Telugu and Kannada layouts and must keep rendering exactly as before.
        assertNull(SvaraMarks.getDisplayText('ा'))  // aa matra
        assertNull(SvaraMarks.getDisplayText('ि'))  // i matra
        assertNull(SvaraMarks.getDisplayText('क'))  // ka
        assertNull(SvaraMarks.getDisplayText('a'))
    }

    @Test
    fun `preference values round-trip`() {
        assertEquals(SvaraEncoding.UNICODE, SvaraEncoding.fromPrefValue("unicode"))
        assertEquals(SvaraEncoding.VIJAYADV, SvaraEncoding.fromPrefValue("vijayadv"))
    }

    @Test
    fun `unknown and missing preference values fall back to the default`() {
        assertEquals(SvaraEncoding.DEFAULT, SvaraEncoding.fromPrefValue(null))
        assertEquals(SvaraEncoding.DEFAULT, SvaraEncoding.fromPrefValue(""))
        assertEquals(SvaraEncoding.DEFAULT, SvaraEncoding.fromPrefValue("vijayadv-v2"))
    }

    @Test
    fun `each encoding names the font asset that can render it`() {
        assertEquals("tiro_devanagari_sanskrit", SvaraEncoding.UNICODE.fontAsset)
        assertEquals("vijayadv", SvaraEncoding.VIJAYADV.fontAsset)
    }
}
