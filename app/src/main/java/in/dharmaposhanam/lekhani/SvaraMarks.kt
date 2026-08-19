package `in`.dharmaposhanam.lekhani

/**
 * Vedic svara (tone) marks, in either standard Unicode or VijayaDV PUA encoding.
 *
 * The two encodings exist because no single font covers both:
 *   - VijayaDV has no glyph for U+1CDA (deergha svarita), which is why it
 *     carries the mark in the Private Use Area instead.
 *   - Unicode-native fonts (Tiro Devanagari Sanskrit) cover U+0951/0952/1CDA
 *     but have nothing in the PUA.
 *
 * Text typed in [SvaraEncoding.UNICODE] is portable to any Unicode-aware
 * application; text typed in [SvaraEncoding.VIJAYADV] renders only where the
 * VijayaDV font is installed.
 */
enum class SvaraEncoding(val prefValue: String, val fontAsset: String) {
    /** Standard Unicode Vedic tone marks. */
    UNICODE("unicode", "tiro_devanagari_sanskrit"),

    /** VijayaDV Private Use Area characters. */
    VIJAYADV("vijayadv", "vijayadv");

    companion object {
        val DEFAULT = UNICODE

        /** Resolve a stored preference value, falling back to [DEFAULT]. */
        fun fromPrefValue(value: String?): SvaraEncoding =
            entries.firstOrNull { it.prefValue == value } ?: DEFAULT
    }
}

object SvaraMarks {
    // Standard Unicode Vedic tone marks.
    // U+0951 is formally DEVANAGARI STRESS SIGN UDATTA; Vedic print convention
    // uses it for svarita, which is the role it plays here.
    const val SVARITA_UNICODE = '\u0951'
    const val ANUDATTA_UNICODE = '\u0952'         // ॒ DEVANAGARI STRESS SIGN ANUDATTA
    const val DEERGHA_SVARITA_UNICODE = '\u1CDA'  // ᳚ VEDIC TONE DOUBLE SVARITA

    // VijayaDV Private Use Area characters
    const val SVARITA_PUA = '\uE302'         // स्वरित
    const val ANUDATTA_PUA = '\uE301'        // अनुदात्त
    const val DEERGHA_SVARITA_PUA = '\uE303' // दीर्घ स्वरित

    /** Base vowel used to render a combining mark legibly on a key. */
    const val DISPLAY_BASE = '\u0905'

    var encoding: SvaraEncoding = SvaraEncoding.DEFAULT
        private set

    val svarita: Char
        get() = if (encoding == SvaraEncoding.UNICODE) SVARITA_UNICODE else SVARITA_PUA

    val anudatta: Char
        get() = if (encoding == SvaraEncoding.UNICODE) ANUDATTA_UNICODE else ANUDATTA_PUA

    val deerghaSvarita: Char
        get() = if (encoding == SvaraEncoding.UNICODE) DEERGHA_SVARITA_UNICODE else DEERGHA_SVARITA_PUA

    /** The three svara marks in the active encoding, as single-character strings. */
    fun marks(): List<String> =
        listOf("$svarita", "$anudatta", "$deerghaSvarita")

    fun setEncoding(encoding: SvaraEncoding) {
        this.encoding = encoding
    }

    /** True for any svara mark, in either encoding. */
    fun isSvaraMark(char: Char): Boolean = when (char) {
        SVARITA_UNICODE, ANUDATTA_UNICODE, DEERGHA_SVARITA_UNICODE,
        SVARITA_PUA, ANUDATTA_PUA, DEERGHA_SVARITA_PUA -> true
        else -> false
    }

    /**
     * Display text for a svara mark: the mark alone renders as a dotted circle,
     * so it is shown applied to a base vowel.
     */
    fun getDisplayText(char: Char): String? =
        if (isSvaraMark(char)) "$DISPLAY_BASE$char" else null
}
