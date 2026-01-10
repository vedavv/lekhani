package `in`.dharmaposhanam.lekhani

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.util.TypedValue
import androidx.core.content.ContextCompat

/**
 * Main Settings Activity
 *
 * Displayed when the app is launched from the launcher.
 * Provides instructions and buttons to:
 * 1. Enable the keyboard in system settings
 * 2. Select the keyboard as active input method
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 96, 48, 48)
            setBackgroundColor(ContextCompat.getColor(context, R.color.keyboard_background))
        }

        // Title
        val titleText = TextView(this).apply {
            text = getString(R.string.app_name)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            setTextColor(ContextCompat.getColor(context, R.color.key_text))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 48)
        }
        layout.addView(titleText)

        // Subtitle / Instructions
        val instructionsText = TextView(this).apply {
            text = getString(R.string.setup_instructions)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(ContextCompat.getColor(context, R.color.key_text_secondary))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 48)
        }
        layout.addView(instructionsText)

        // Step 1: Enable Keyboard
        val step1Title = TextView(this).apply {
            text = getString(R.string.step1_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(ContextCompat.getColor(context, R.color.key_text))
            setPadding(0, 24, 0, 8)
        }
        layout.addView(step1Title)

        val step1Desc = TextView(this).apply {
            text = getString(R.string.step1_desc)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(ContextCompat.getColor(context, R.color.key_text_secondary))
            setPadding(0, 0, 0, 16)
        }
        layout.addView(step1Desc)

        val enableButton = Button(this).apply {
            text = getString(R.string.enable_keyboard_title)
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
        }
        layout.addView(enableButton)

        // Step 2: Select Keyboard
        val step2Title = TextView(this).apply {
            text = getString(R.string.step2_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(ContextCompat.getColor(context, R.color.key_text))
            setPadding(0, 48, 0, 8)
        }
        layout.addView(step2Title)

        val step2Desc = TextView(this).apply {
            text = getString(R.string.step2_desc)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(ContextCompat.getColor(context, R.color.key_text_secondary))
            setPadding(0, 0, 0, 16)
        }
        layout.addView(step2Desc)

        val selectButton = Button(this).apply {
            text = getString(R.string.select_keyboard_title)
            setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        }
        layout.addView(selectButton)

        // Keyboard Settings
        val settingsTitle = TextView(this).apply {
            text = getString(R.string.keyboard_settings_title)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(ContextCompat.getColor(context, R.color.key_text))
            setPadding(0, 48, 0, 8)
        }
        layout.addView(settingsTitle)

        val settingsButton = Button(this).apply {
            text = getString(R.string.ime_settings)
            setOnClickListener {
                startActivity(Intent(this@SettingsActivity, ImeSettingsActivity::class.java))
            }
        }
        layout.addView(settingsButton)

        // Status indicator
        val statusText = TextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            gravity = Gravity.CENTER
            setPadding(0, 48, 0, 0)
            updateStatus(this)
        }
        layout.addView(statusText)

        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        // Update status when returning from settings
    }

    private fun updateStatus(textView: TextView) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val enabledMethods = imm.enabledInputMethodList

        val isEnabled = enabledMethods.any {
            it.packageName == packageName
        }

        if (isEnabled) {
            textView.text = getString(R.string.status_enabled)
            textView.setTextColor(ContextCompat.getColor(this, R.color.status_enabled))
        } else {
            textView.text = getString(R.string.status_disabled)
            textView.setTextColor(ContextCompat.getColor(this, R.color.status_disabled))
        }
    }
}
