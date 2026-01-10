package `in`.dharmaposhanam.lekhani

/**
 * Kannada Keyboard Layout Definition (InScript)
 *
 * Based on iOS Kannada keyboard with InScript layout.
 * Unicode range: U+0C80-U+0CFF (Kannada only)
 */

/**
 * Long-press popup mappings for alternate characters
 * Based on iOS Kannada keyboard behavior
 */
object PopupMappings {
    private val baseMappings: Map<String, List<String>> = mapOf(
        // Independent vowels - long press reveals extended forms
        "ಅ" to listOf("ಆ", "ಽ"),
        "ಇ" to listOf("ಈ"),
        "ಉ" to listOf("ಊ"),
        "ಎ" to listOf("ಏ", "ಐ"),
        "ಒ" to listOf("ಓ", "ಔ"),
        "ಋ" to listOf("ೠ"),
        "ಌ" to listOf("ೡ"),

        // Dependent vowel matras - long press reveals extended forms
        "ಾ" to listOf("ಆ"),
        "ಿ" to listOf("ೀ", "ಇ", "ಈ"),
        "ು" to listOf("ೂ", "ಉ", "ಊ"),
        "ೆ" to listOf("ೇ", "ಎ", "ಏ"),
        "ೊ" to listOf("ೋ", "ಒ", "ಓ"),
        "ೀ" to listOf("ಿ", "ಈ", "ಇ"),
        "ೂ" to listOf("ು", "ಊ", "ಉ"),
        "ೇ" to listOf("ೆ", "ಏ", "ಎ"),
        "ೋ" to listOf("ೊ", "ಓ", "ಒ"),
        "ೈ" to listOf("ಐ"),
        "ೌ" to listOf("ಔ"),
        "ೃ" to listOf("ೄ", "ಋ", "ೠ"),

        // Consonants with aspirated forms
        "ಕ" to listOf("ಖ"),
        "ಖ" to listOf("ಕ"),
        "ಗ" to listOf("ಘ"),
        "ಘ" to listOf("ಗ"),
        "ಚ" to listOf("ಛ"),
        "ಛ" to listOf("ಚ"),
        "ಜ" to listOf("ಝ"),
        "ಝ" to listOf("ಜ"),
        "ಟ" to listOf("ಠ"),
        "ಠ" to listOf("ಟ"),
        "ಡ" to listOf("ಢ"),
        "ಢ" to listOf("ಡ"),
        "ತ" to listOf("ಥ"),
        "ಥ" to listOf("ತ"),
        "ದ" to listOf("ಧ"),
        "ಧ" to listOf("ದ"),
        "ಪ" to listOf("ಫ"),
        "ಫ" to listOf("ಪ"),
        "ಬ" to listOf("ಭ"),
        "ಭ" to listOf("ಬ"),
        "ಶ" to listOf("ಷ", "ಸ"),
        "ಷ" to listOf("ಶ", "ಸ"),
        "ಸ" to listOf("ಶ", "ಷ"),

        // Nasals
        "ನ" to listOf("ಣ", "ಞ", "ಙ"),
        "ಣ" to listOf("ನ"),
        "ಞ" to listOf("ನ"),
        "ಙ" to listOf("ನ"),
        "ಮ" to listOf("ಂ", "ಁ"),

        // Anusvara and Chandrabindu
        "ಂ" to listOf("ಁ", "ಃ"),
        "ಁ" to listOf("ಂ", "ಃ"),

        // Kannada digits - long press for related
        "೧" to listOf("೨", "೩"),
        "೪" to listOf("೫", "೬"),
        "೭" to listOf("೮", "೯"),
        "೦" to listOf("ಓಂ"),

        // Semivowels
        "ಯ" to listOf("ಞ"),
        "ರ" to listOf("ಱ"),
        "ಲ" to listOf("ಳ"),
        "ಳ" to listOf("ಲ"),

        // Sibilants
        "ಹ" to listOf("ಃ"),

        // Avagraha
        "ಽ" to listOf("ಅ")
    )

    fun getAlternates(key: String): List<String>? = baseMappings[key]

    fun hasAlternates(key: String): Boolean = baseMappings.containsKey(key)
}

/**
 * Complete keyboard layout with all pages
 * Kannada InScript layout - Only Kannada Unicode (U+0C80-U+0CFF)
 */
object KeyboardLayouts {

    // Main page rows - InScript layout with Kannada digits
    private val mainPageRows = listOf(
        // Kannada Numbers (U+0CE6-U+0CEF)
        listOf("೧", "೨", "೩", "೪", "೫", "೬", "೭", "೮", "೯", "೦"),
        // Vowel matras + consonants row 1
        listOf("ೌ", "ೈ", "ಾ", "ೀ", "ೂ", "ಬ", "ಹ", "ಗ", "ದ", "ಜ", "ಡ"),
        // Vowel matras + consonants row 2
        listOf("ೋ", "ೇ", "್", "ಿ", "ು", "ಪ", "ರ", "ಕ", "ತ", "ಚ", "ಟ"),
        // Special + consonants row 3
        listOf(SpecialKey.SHIFT.display, "ಁ", "ಂ", "ಮ", "ನ", "ವ", "ಲ", "ಸ", "ಯ", SpecialKey.BACKSPACE.display),
        // Bottom row
        listOf(SpecialKey.SPACE.display, SpecialKey.RETURN.display)
    )

    // Shift page rows - All Kannada characters
    private val shiftPageRows = listOf(
        // Additional Kannada characters (vowel signs, rare vowels, signs)
        listOf("ೃ", "ೄ", "ೢ", "ೣ", "ೕ", "ೖ", "ೱ", "ೲ", "಼", "ಽ"),
        // Independent vowels + aspirated consonants row 1
        listOf("ಔ", "ಐ", "ಆ", "ಈ", "ಊ", "ಭ", "ಙ", "ಘ", "ಧ", "ಝ", "ಢ"),
        // Independent vowels + aspirated consonants row 2
        listOf("ಓ", "ಏ", "ಅ", "ಇ", "ಉ", "ಫ", "ಞ", "ಖ", "ಥ", "ಛ", "ಠ"),
        // Special consonants and rare characters
        listOf(SpecialKey.SHIFT.display, "ಷ", "ಋ", "ಣ", "ಳ", "ಶ", "ಃ", "ಱ", "ಌ", SpecialKey.BACKSPACE.display),
        // Bottom row
        listOf(SpecialKey.SPACE.display, SpecialKey.RETURN.display)
    )

    /**
     * Get the main page layout
     */
    fun getMainPage(): KeyboardPage {
        return KeyboardPage(name = "ಮುಖ್ಯ", rows = mainPageRows)
    }

    /**
     * Get the shift page layout
     */
    fun getShiftPage(): KeyboardPage {
        return KeyboardPage(name = "ಶಿಫ್ಟ್", rows = shiftPageRows)
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
