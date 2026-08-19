package `in`.dharmaposhanam.lekhani

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.text.BreakIterator

/**
 * Lekhani Input Method Service
 *
 * Generic InputMethodService for all Indic language keyboards.
 * Language-specific behavior is provided by KeyboardLayouts and PopupMappings
 * objects which are defined per-flavor.
 */
class LekhaniInputMethodService : InputMethodService(), LekhaniKeyboardView.KeyboardActionListener {

    private var keyboardView: LekhaniKeyboardView? = null
    private var currentPageIndex = 0
    private var vibrator: Vibrator? = null
    private var vibrateOnKeypress = true
    private var customFont: Typeface? = null

    override fun onCreate() {
        super.onCreate()

        // Initialize vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Load custom font if available
        loadCustomFont()

        // Load preferences
        loadPreferences()
    }

    private fun loadCustomFont() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        customFont = FontLoader.load(this, prefs.getString(PREF_FONT, null))
    }

    private fun loadPreferences() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        vibrateOnKeypress = prefs.getBoolean("pref_vibrate", true)
        SvaraMarks.setEncoding(
            SvaraEncoding.fromPrefValue(prefs.getString(PREF_SVARA_ENCODING, null))
        )
    }

    /**
     * Persist an encoding choice and move the display font with it.
     *
     * The two are coupled because neither font can draw the other's marks:
     * VijayaDV has no glyph at U+1CDA, and the Unicode font has nothing in the
     * PUA. Leaving them independent lets a user select a combination that
     * silently renders every svara as a blank box.
     */
    private fun applySvaraEncoding(encoding: SvaraEncoding) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
            .putString(PREF_SVARA_ENCODING, encoding.prefValue)
            .putString(PREF_FONT, encoding.fontAsset)
            .apply()

        SvaraMarks.setEncoding(encoding)
        loadCustomFont()

        keyboardView?.setCustomFont(customFont)
        keyboardView?.setKeyboardPage(KeyboardLayouts.getPage(currentPageIndex), currentPageIndex)
    }

    override fun onCreateInputView(): View {
        keyboardView = LekhaniKeyboardView(this).apply {
            setKeyboardActionListener(this@LekhaniInputMethodService)
            setCustomFont(customFont)
            setKeyboardPage(KeyboardLayouts.getPage(currentPageIndex), currentPageIndex)
        }
        return keyboardView!!
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        // Reset to main page when starting input
        currentPageIndex = 0

        // Reload preferences in case they changed
        loadPreferences()

        // Reload font in case it changed
        loadCustomFont()
        keyboardView?.setCustomFont(customFont)

        // Update keyboard with current settings
        keyboardView?.setKeyboardPage(KeyboardLayouts.getPage(currentPageIndex), currentPageIndex)
    }

    override fun onFinishInput() {
        super.onFinishInput()
        currentPageIndex = 0
    }

    // KeyboardActionListener implementation

    override fun onKeyPressed(key: String) {
        performHapticFeedback()

        when (key) {
            SpecialKey.BACKSPACE.display -> handleBackspace()
            SpecialKey.SPACE.display -> handleSpace()
            SpecialKey.SHIFT.display -> handleShift()
            SpecialKey.RETURN.display -> handleReturn()
            SpecialKey.SETTINGS.display -> Unit  // The view opens the settings panel
            else -> handleCharacterInput(key)
        }
    }

    override fun onKeyLongPressed(key: String) {
        // Long press on spacebar shows keyboard picker
        if (key == SpecialKey.SPACE.display) {
            performHapticFeedback()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showInputMethodPicker()
        }
        // Other long presses handled by popup in view
    }

    override fun onPopupKeySelected(key: String) {
        performHapticFeedback()
        handleCharacterInput(key)
    }

    override fun onCursorMove(direction: Int) {
        val ic = currentInputConnection ?: return

        // Get current cursor position info
        val extracted = ic.getExtractedText(android.view.inputmethod.ExtractedTextRequest(), 0)
        if (extracted != null) {
            val currentPos = extracted.selectionStart
            val newPos = currentPos + direction

            // Ensure we stay within bounds
            if (newPos >= 0 && newPos <= extracted.text.length) {
                ic.setSelection(newPos, newPos)
                // Light haptic feedback for cursor movement
                performLightHapticFeedback()
            }
        }
    }

    override fun onSvaraEncodingChanged(encoding: SvaraEncoding) {
        applySvaraEncoding(encoding)
        val label = when (encoding) {
            SvaraEncoding.UNICODE -> getString(R.string.svara_encoding_unicode)
            SvaraEncoding.VIJAYADV -> getString(R.string.svara_encoding_vijayadv)
        }
        toast(getString(R.string.svara_encoding_applied, label))
    }

    override fun onOpenFullSettings() {
        val intent = Intent(this, ImeSettingsActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDownloadVijayaDvFont() {
        when (val result = FontExporter.exportVijayaDv(this)) {
            is FontExporter.Result.SavedToDownloads ->
                toast(getString(R.string.font_saved_to_downloads))
            is FontExporter.Result.Shared -> Unit  // The chooser is its own feedback
            is FontExporter.Result.Failed ->
                toast(getString(R.string.font_export_failed, result.cause.message ?: ""))
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleCharacterInput(text: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(text, 1)
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return

        // Get text before cursor to handle grapheme clusters properly
        val textBefore = ic.getTextBeforeCursor(2, 0)

        if (textBefore.isNullOrEmpty()) return

        // Delete one character for now; more sophisticated grapheme
        // cluster handling can be added later
        ic.deleteSurroundingText(1, 0)
    }

    private fun handleSpace() {
        val ic = currentInputConnection ?: return
        ic.commitText(" ", 1)
    }

    private fun handleShift() {
        currentPageIndex = if (currentPageIndex == 0) 1 else 0
        keyboardView?.setKeyboardPage(KeyboardLayouts.getPage(currentPageIndex), currentPageIndex)
    }

    private fun handleReturn() {
        val ic = currentInputConnection ?: return
        val editorInfo = currentInputEditorInfo

        // Check what action is expected
        val actionId = editorInfo?.imeOptions?.and(
            EditorInfo.IME_MASK_ACTION or EditorInfo.IME_FLAG_NO_ENTER_ACTION
        ) ?: EditorInfo.IME_ACTION_UNSPECIFIED

        when (actionId) {
            EditorInfo.IME_ACTION_SEARCH,
            EditorInfo.IME_ACTION_SEND,
            EditorInfo.IME_ACTION_GO,
            EditorInfo.IME_ACTION_DONE -> {
                ic.performEditorAction(actionId)
            }
            EditorInfo.IME_ACTION_NEXT -> {
                ic.performEditorAction(EditorInfo.IME_ACTION_NEXT)
            }
            else -> {
                // Default: send newline
                ic.commitText("\n", 1)
            }
        }
    }

    private fun performHapticFeedback() {
        if (!vibrateOnKeypress) return

        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(10)
            }
        }
    }

    private fun performLightHapticFeedback() {
        if (!vibrateOnKeypress) return

        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(5)
            }
        }
    }

    companion object {
        private const val PREF_FONT = "pref_font"
        private const val PREF_SVARA_ENCODING = "pref_svara_encoding"
    }
}
