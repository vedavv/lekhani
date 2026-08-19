package `in`.dharmaposhanam.lekhani

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SvaraMarksTest {

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

        assertEquals('॑', SvaraMarks.svarita)
        assertEquals('॒', SvaraMarks.anudatta)
        assertEquals('᳚', SvaraMarks.deerghaSvarita)
    }

    @Test
    fun `vijayadv encoding emits the private use area codepoints`() {
        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)

        assertEquals('', SvaraMarks.svarita)
        assertEquals('', SvaraMarks.anudatta)
        assertEquals('', SvaraMarks.deerghaSvarita)
    }

    @Test
    fun `deergha svarita is U+1CDA in unicode mode`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)

        assertEquals(0x1CDA, SvaraMarks.deerghaSvarita.code)
    }

    @Test
    fun `marks follow the active encoding`() {
        SvaraMarks.setEncoding(SvaraEncoding.UNICODE)
        assertEquals(listOf("॑", "॒", "᳚"), SvaraMarks.marks())

        SvaraMarks.setEncoding(SvaraEncoding.VIJAYADV)
        assertEquals(listOf("", "", ""), SvaraMarks.marks())
    }

    @Test
    fun `svara marks are recognised in either encoding`() {
        listOf('॑', '॒', '᳚', '', '', '').forEach {
            assertTrue("expected $it to be a svara mark", SvaraMarks.isSvaraMark(it))
        }
    }

    @Test
    fun `ordinary letters are not svara marks`() {
        listOf('अ', 'a', 'ं').forEach {
            assertFalse("expected $it not to be a svara mark", SvaraMarks.isSvaraMark(it))
        }
    }

    @Test
    fun `display text puts the mark on a base vowel`() {
        assertEquals("अ॑", SvaraMarks.getDisplayText('॑'))
        assertEquals("अ᳚", SvaraMarks.getDisplayText('᳚'))
    }

    @Test
    fun `display text is null for a non-mark`() {
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

    @Test
    fun `combining marks are shown on a base vowel`() {
        assertEquals("अ॑", CombiningMarks.getDisplayText("॑"))
        assertEquals("अा", CombiningMarks.getDisplayText("ा"))
        assertEquals("अ", CombiningMarks.getDisplayText(""))
    }

    @Test
    fun `base consonants are left alone`() {
        assertNull(CombiningMarks.getDisplayText("क"))
        assertNull(CombiningMarks.getDisplayText("अ"))
        assertNull(CombiningMarks.getDisplayText("क्ष"))
    }
}
