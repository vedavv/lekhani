package `in`.dharmaposhanam.lekhani

/**
 * Telugu Keyboard Layout Definition (InScript)
 *
 * Based on iOS Telugu keyboard with InScript layout.
 * Unicode range: U+0C00-U+0C7F (Telugu only)
 */

/**
 * Long-press popup mappings for alternate characters
 * Based on iOS Telugu keyboard behavior
 */
object PopupMappings {
    private val baseMappings: Map<String, List<String>> = mapOf(
        // Independent vowels - long press reveals extended forms
        "అ" to listOf("ఆ", "ఽ"),
        "ఇ" to listOf("ఈ"),
        "ఉ" to listOf("ఊ"),
        "ఎ" to listOf("ఏ", "ఐ"),
        "ఒ" to listOf("ఓ", "ఔ"),
        "ఋ" to listOf("ౠ"),
        "ఌ" to listOf("ౡ"),

        // Dependent vowel matras - long press reveals extended forms
        "ా" to listOf("ఆ"),
        "ి" to listOf("ీ", "ఇ", "ఈ"),
        "ు" to listOf("ూ", "ఉ", "ఊ"),
        "ె" to listOf("ే", "ఎ", "ఏ"),
        "ొ" to listOf("ో", "ఒ", "ఓ"),
        "ీ" to listOf("ి", "ఈ", "ఇ"),
        "ూ" to listOf("ు", "ఊ", "ఉ"),
        "ే" to listOf("ె", "ఏ", "ఎ"),
        "ో" to listOf("ొ", "ఓ", "ఒ"),
        "ై" to listOf("ఐ"),
        "ౌ" to listOf("ఔ"),
        "ృ" to listOf("ౄ", "ఋ", "ౠ"),

        // Consonants with aspirated forms
        "క" to listOf("ఖ"),
        "ఖ" to listOf("క"),
        "గ" to listOf("ఘ"),
        "ఘ" to listOf("గ"),
        "చ" to listOf("ఛ"),
        "ఛ" to listOf("చ"),
        "జ" to listOf("ఝ"),
        "ఝ" to listOf("జ"),
        "ట" to listOf("ఠ"),
        "ఠ" to listOf("ట"),
        "డ" to listOf("ఢ"),
        "ఢ" to listOf("డ"),
        "త" to listOf("థ"),
        "థ" to listOf("త"),
        "ద" to listOf("ధ"),
        "ధ" to listOf("ద"),
        "ప" to listOf("ఫ"),
        "ఫ" to listOf("ప"),
        "బ" to listOf("భ"),
        "భ" to listOf("బ"),
        "శ" to listOf("ష", "స"),
        "ష" to listOf("శ", "స"),
        "స" to listOf("శ", "ష"),

        // Nasals
        "న" to listOf("ణ", "ఞ", "ఙ"),
        "ణ" to listOf("న"),
        "ఞ" to listOf("న"),
        "ఙ" to listOf("న"),
        "మ" to listOf("ం", "ఁ"),

        // Anusvara and Chandrabindu
        "ం" to listOf("ఁ", "ః"),
        "ఁ" to listOf("ం", "ః"),

        // Roman digits - long press shows Telugu digits
        "1" to listOf("౧"),
        "2" to listOf("౨"),
        "3" to listOf("౩"),
        "4" to listOf("౪"),
        "5" to listOf("౫"),
        "6" to listOf("౬"),
        "7" to listOf("౭"),
        "8" to listOf("౮"),
        "9" to listOf("౯"),
        "0" to listOf("౦"),

        // Semivowels
        "య" to listOf("ఞ"),
        "ర" to listOf("ఱ"),
        "ల" to listOf("ళ"),
        "ళ" to listOf("ల"),

        // Sibilants
        "హ" to listOf("ః"),

        // Avagraha
        "ఽ" to listOf("అ")
    )

    fun getAlternates(key: String): List<String>? = baseMappings[key]

    fun hasAlternates(key: String): Boolean = baseMappings.containsKey(key)
}

/**
 * Complete keyboard layout with all pages
 * Telugu InScript layout - Only Telugu Unicode (U+0C00-U+0C7F)
 */
object KeyboardLayouts {

    // Main page rows - InScript layout with Roman digits (Telugu on long-press)
    private val mainPageRows = listOf(
        // Roman Numbers (long-press for Telugu digits)
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        // Vowel matras + consonants row 1
        listOf("ౌ", "ై", "ా", "ీ", "ూ", "బ", "హ", "గ", "ద", "జ", "డ"),
        // Vowel matras + consonants row 2
        listOf("ో", "ే", "్", "ి", "ు", "ప", "ర", "క", "త", "చ", "ట"),
        // Special + consonants row 3
        listOf(SpecialKey.SHIFT.display, "ఁ", "ం", "మ", "న", "వ", "ల", "స", "య", SpecialKey.BACKSPACE.display),
        // Bottom row
        listOf(SpecialKey.SPACE.display, SpecialKey.RETURN.display)
    )

    // Shift page rows - All Telugu characters
    private val shiftPageRows = listOf(
        // Additional Telugu characters (vowel signs, rare vowels, signs)
        listOf("ృ", "ౄ", "ౢ", "ౣ", "ౕ", "ౖ", "ఀ", "౷", "౿", "ఽ"),
        // Independent vowels + aspirated consonants row 1
        listOf("ఔ", "ఐ", "ఆ", "ఈ", "ఊ", "భ", "ఙ", "ఘ", "ధ", "ఝ", "ఢ"),
        // Independent vowels + aspirated consonants row 2
        listOf("ఓ", "ఏ", "అ", "ఇ", "ఉ", "ఫ", "ఞ", "ఖ", "థ", "ఛ", "ఠ"),
        // Special consonants and rare characters
        listOf(SpecialKey.SHIFT.display, "ష", "ఋ", "ణ", "ళ", "శ", "ః", "ఱ", "ఌ", SpecialKey.BACKSPACE.display),
        // Bottom row
        listOf(SpecialKey.SPACE.display, SpecialKey.RETURN.display)
    )

    /**
     * Get the main page layout
     */
    fun getMainPage(): KeyboardPage {
        return KeyboardPage(name = "ప్రధాన", rows = mainPageRows)
    }

    /**
     * Get the shift page layout
     */
    fun getShiftPage(): KeyboardPage {
        return KeyboardPage(name = "షిఫ్ట్", rows = shiftPageRows)
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
