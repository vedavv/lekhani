package `in`.dharmaposhanam.lekhani.keyboard

/**
 * Keyboard page/layer definition
 */
data class KeyboardPage(
    val name: String,
    val rows: List<List<String>>
)

/**
 * Special key codes - shared across all language keyboards
 */
enum class SpecialKey(val display: String) {
    BACKSPACE("⌫"),
    SHIFT("⇧"),
    SPACE(" "),  // Will be customized per language
    RETURN("return"),
    DONE("Done")
}
