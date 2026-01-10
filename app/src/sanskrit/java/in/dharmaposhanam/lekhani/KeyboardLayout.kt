package `in`.dharmaposhanam.lekhani

/**
 * Sanskrit Keyboard Layout Definition (InScript)
 *
 * Based on iOS Devanagari keyboard with InScript layout.
 * Unicode range: U+0900-U+097F
 * Includes Vedic extensions for svara marks.
 */

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

        // Anusvara and Chandrabindu (with Vedic svara marks)
        "ं" to listOf("ँ", "ः", "॑", "॒"),
        "ँ" to listOf("ं", "ः", "॑", "॒"),

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

        // Vedic svara marks
        "॑" to listOf("॒", "᳚"),
        "॒" to listOf("॑", "᳚")
    )

    fun getAlternates(key: String): List<String>? = baseMappings[key]

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
}
