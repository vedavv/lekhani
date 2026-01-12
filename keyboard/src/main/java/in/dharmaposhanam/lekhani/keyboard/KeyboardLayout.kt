package `in`.dharmaposhanam.lekhani.keyboard

/**
 * Sanskrit Keyboard Layout Definition (InScript)
 *
 * Based on iOS Devanagari keyboard with InScript layout.
 * Unicode range: U+0900-U+097F
 * Includes Vedic extensions for svara marks.
 */

/**
 * Vedic Svara Marks - Standard Unicode and VijayaDV PUA variants
 *
 * VijayaDV font uses Private Use Area (PUA) characters for svara marks
 * which render better than standard Unicode in that font.
 */
object SvaraMarks {
    // Standard Unicode Vedic tone marks (default)
    const val SVARITA_UNICODE = '\u0951'        // ॑ DEVANAGARI STRESS SIGN UDATTA (svarita)
    const val ANUDATTA_UNICODE = '\u0952'       // ॒ DEVANAGARI STRESS SIGN ANUDATTA
    const val DEERGHA_SVARITA_UNICODE = '\u1CDA' // ᳚ VEDIC TONE DOUBLE SVARITA

    // VijayaDV Private Use Area characters
    const val SVARITA_PUA = '\uE302'        // स्वरित
    const val ANUDATTA_PUA = '\uE301'       // अनुदात्त
    const val DEERGHA_SVARITA_PUA = '\uE303' // दीर्घ स्वरित

    // Current active characters (default to Unicode)
    var SVARITA = SVARITA_UNICODE
        private set
    var ANUDATTA = ANUDATTA_UNICODE
        private set
    var DEERGHA_SVARITA = DEERGHA_SVARITA_UNICODE
        private set

    // Track current mode
    var usePua = false
        private set

    /**
     * Switch between PUA and Unicode characters for svara marks.
     * Call this before building keyboard to use PUA characters.
     * @param usePua If true, use VijayaDV PUA characters; if false, use standard Unicode
     */
    fun usePuaCharacters(usePua: Boolean) {
        this.usePua = usePua
        if (usePua) {
            SVARITA = SVARITA_PUA
            ANUDATTA = ANUDATTA_PUA
            DEERGHA_SVARITA = DEERGHA_SVARITA_PUA
        } else {
            SVARITA = SVARITA_UNICODE
            ANUDATTA = ANUDATTA_UNICODE
            DEERGHA_SVARITA = DEERGHA_SVARITA_UNICODE
        }
    }

    /**
     * Check if a character is a svara mark (either Unicode or PUA)
     */
    fun isSvaraMark(char: Char): Boolean {
        return char == SVARITA_UNICODE || char == ANUDATTA_UNICODE ||
               char == DEERGHA_SVARITA_UNICODE ||
               char == SVARITA_PUA || char == ANUDATTA_PUA ||
               char == DEERGHA_SVARITA_PUA
    }

    /**
     * Get display text showing base vowel with svara mark
     */
    fun getDisplayText(char: Char): String? {
        return when (char) {
            SVARITA, SVARITA_UNICODE, SVARITA_PUA -> "अ$SVARITA"
            ANUDATTA, ANUDATTA_UNICODE, ANUDATTA_PUA -> "अ$ANUDATTA"
            DEERGHA_SVARITA, DEERGHA_SVARITA_UNICODE, DEERGHA_SVARITA_PUA -> "अ$DEERGHA_SVARITA"
            else -> null
        }
    }
}

/**
 * Helper for displaying combining Devanagari diacritics with a base character.
 * Combining marks render as dotted circles when displayed alone.
 */
object CombiningMarks {
    // Devanagari combining marks that need a base character for display
    private val combiningRanges = listOf(
        '\u0901'..'\u0903',  // Chandrabindu, Anusvara, Visarga
        '\u093A'..'\u093C',  // Nukta and friends
        '\u093E'..'\u094F',  // Dependent vowel signs (matras)
        '\u0951'..'\u0957',  // Vedic tone marks
        '\u0962'..'\u0963',  // Vowel signs for vocalic L
        '\u1CDA'..'\u1CDA',  // Vedic double svarita
    )

    // PUA characters also need base
    private val puaMarks = setOf(
        '\uE301', '\uE302', '\uE303'  // Lekhani PUA svara marks
    )

    /**
     * Check if a character is a combining mark that needs a base for display
     */
    fun isCombiningMark(char: Char): Boolean {
        if (puaMarks.contains(char)) return true
        return combiningRanges.any { char in it }
    }

    /**
     * Get display text for a combining mark (adds base vowel अ)
     */
    fun getDisplayText(text: String): String? {
        if (text.length != 1) return null
        val char = text[0]
        // For svara marks, use SvaraMarks which respects PUA mode
        SvaraMarks.getDisplayText(char)?.let { return it }
        // For other combining marks, add base vowel
        if (isCombiningMark(char)) {
            return "अ$char"
        }
        return null
    }
}

/**
 * Long-press popup mappings for alternate characters
 * Based on iOS Devanagari keyboard behavior
 */
object PopupMappings {
    private val baseMappings: Map<String, List<String>> = mapOf(
        // Independent vowels - long press reveals extended forms
        "अ" to listOf("आ", "ऑ"),
        "इ" to listOf("ई"),
        "उ" to listOf("ऊ"),
        "ए" to listOf("ऐ"),
        "ओ" to listOf("औ"),
        "ऋ" to listOf("ॠ", "ॄ"),

        // Dependent vowel matras - long press reveals extended forms
        "ा" to listOf("ॉ", "आ"),
        "ि" to listOf("ी", "इ", "ई"),
        "ु" to listOf("ू", "उ", "ऊ"),
        "े" to listOf("ै", "ए", "ऐ"),
        "ो" to listOf("ौ", "ओ", "औ"),
        "ी" to listOf("ि", "ई", "इ"),
        "ू" to listOf("ु", "ऊ", "उ"),
        "ै" to listOf("े", "ऐ", "ए"),
        "ौ" to listOf("ो", "औ", "ओ"),

        // Consonants with aspirated forms and nuqta variants
        "क" to listOf("ख", "क़", "क्ष"),
        "ख" to listOf("क", "ख़"),
        "ग" to listOf("घ", "ग़"),
        "घ" to listOf("ग"),
        "च" to listOf("छ"),
        "छ" to listOf("च"),
        "ज" to listOf("झ", "ज़", "ज्ञ"),
        "झ" to listOf("ज"),
        "ट" to listOf("ठ"),
        "ठ" to listOf("ट"),
        "ड" to listOf("ढ", "ड़"),
        "ढ" to listOf("ड", "ढ़"),
        "त" to listOf("थ", "त्र"),
        "थ" to listOf("त"),
        "द" to listOf("ध"),
        "ध" to listOf("द"),
        "प" to listOf("फ"),
        "फ" to listOf("प", "फ़"),
        "ब" to listOf("भ"),
        "भ" to listOf("ब"),
        "श" to listOf("ष", "श्र"),
        "ष" to listOf("श"),

        // Nasals
        "न" to listOf("ण", "ञ", "ङ"),
        "ण" to listOf("न"),
        "ञ" to listOf("न"),
        "ङ" to listOf("न"),
        "म" to listOf("ं", "ँ"),

        // Anusvara and Chandrabindu (svara marks added dynamically)
        "ं" to listOf("ँ", "ः"),
        "ँ" to listOf("ं", "ः"),

        // Conjuncts on shift page
        "क्ष" to listOf("क", "ष"),
        "ज्ञ" to listOf("ज", "ञ"),
        "श्र" to listOf("श", "र"),

        // Numbers - Arabic to Devanagari
        "0" to listOf("०"),
        "1" to listOf("१"),
        "2" to listOf("२"),
        "3" to listOf("३"),
        "4" to listOf("४"),
        "5" to listOf("५"),
        "6" to listOf("६"),
        "7" to listOf("७"),
        "8" to listOf("८"),
        "9" to listOf("९"),

        // Special characters
        "।" to listOf("॥"),
        "०" to listOf("ॐ"),

        // Semivowels
        "य" to listOf("य़"),
        "र" to listOf("ऱ"),
        "ल" to listOf("ळ"),
        "ळ" to listOf("ल"),
        "व" to listOf("व़"),

        // Sibilants
        "स" to listOf("श", "ष"),
        "ह" to listOf("ः"),

        // Vedic svara marks - alternates added dynamically based on PUA mode
        "॑" to listOf("॒"),
        "॒" to listOf("॑")
    )

    // Keys that should have svara marks added dynamically
    private val svaraKeys = setOf("ं", "ँ")

    /**
     * Get alternate characters for a key.
     * Dynamically adds svara marks to anusvara/chandrabindu based on current SvaraMarks mode.
     */
    fun getAlternates(key: String): List<String>? {
        val base = baseMappings[key] ?: return null
        // Add svara marks to anusvara/chandrabindu keys
        return if (key in svaraKeys) {
            base + listOf(
                "${SvaraMarks.SVARITA}",
                "${SvaraMarks.ANUDATTA}",
                "${SvaraMarks.DEERGHA_SVARITA}"
            )
        } else {
            base
        }
    }

    fun hasAlternates(key: String): Boolean = baseMappings.containsKey(key)
}

/**
 * Complete keyboard layout with all pages
 * Sanskrit InScript layout with Vedic support
 */
object KeyboardLayouts {

    // Main page rows - InScript layout
    private val mainPageRows = listOf(
        // Numbers
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        // Vowel matras + consonants row 1
        listOf("ौ", "ै", "ा", "ी", "ू", "ब", "ह", "ग", "द", "ज", "ड"),
        // Vowel matras + consonants row 2
        listOf("ो", "े", "्", "ि", "ु", "प", "र", "क", "त", "च", "ट"),
        // Special + consonants row 3
        listOf(SpecialKey.SHIFT.display, "ँ", "ं", "म", "न", "व", "ल", "स", "य", SpecialKey.BACKSPACE.display),
        // Bottom row
        listOf(SpecialKey.SPACE.display, SpecialKey.RETURN.display)
    )

    // Shift page rows
    private val shiftPageRows = listOf(
        // Symbols
        listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")"),
        // Independent vowels + aspirated consonants row 1
        listOf("औ", "ऐ", "आ", "ई", "ऊ", "भ", "ङ", "घ", "ध", "झ", "ढ"),
        // Independent vowels + aspirated consonants row 2
        listOf("ओ", "ए", "अ", "इ", "उ", "फ", "ञ", "ख", "थ", "छ", "ठ"),
        // Special consonants and conjuncts
        listOf(SpecialKey.SHIFT.display, "ष", "ऋ", "ण", "ळ", "श्र", "ज्ञ", "क्ष", "श", SpecialKey.BACKSPACE.display),
        // Bottom row
        listOf(SpecialKey.SPACE.display, SpecialKey.RETURN.display)
    )

    /**
     * Get the main page layout
     */
    fun getMainPage(): KeyboardPage {
        return KeyboardPage(name = "मुख्य", rows = mainPageRows)
    }

    /**
     * Get the shift page layout
     */
    fun getShiftPage(): KeyboardPage {
        return KeyboardPage(name = "शिफ्ट", rows = shiftPageRows)
    }

    /**
     * Get a keyboard page by index
     * @param index 0 for main page, 1 for shift page
     */
    fun getPage(index: Int): KeyboardPage {
        return when (index) {
            0 -> getMainPage()
            1 -> getShiftPage()
            else -> getMainPage()
        }
    }

    fun isSpecialKey(key: String): Boolean {
        return SpecialKey.entries.any { it.display == key }
    }

    /**
     * Check if a key is a svara mark
     */
    fun isSvaraKey(key: String): Boolean {
        if (key.length != 1) return false
        return SvaraMarks.isSvaraMark(key[0])
    }

    /**
     * Get display text for a svara key (shows base vowel + mark)
     */
    fun getSvaraDisplayText(key: String): String? {
        if (key.length != 1) return null
        return SvaraMarks.getDisplayText(key[0])
    }
}
