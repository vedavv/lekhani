package `in`.dharmaposhanam.lekhani

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.SeekBarPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.ListPreference

/**
 * IME Settings Activity
 *
 * Provides user preferences for the keyboard including:
 * - Haptic feedback toggle
 * - Sound feedback toggle
 * - Key popup preview toggle
 * - Long press delay
 * - Key height adjustment
 */
class ImeSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, ImeSettingsFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.ime_settings)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class ImeSettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)

            // Feedback category
            val feedbackCategory = PreferenceCategory(context).apply {
                title = getString(R.string.pref_category_feedback)
                key = "feedback_category"
            }
            screen.addPreference(feedbackCategory)

            // Vibrate preference
            val vibratePref = SwitchPreferenceCompat(context).apply {
                key = "pref_vibrate"
                title = getString(R.string.pref_vibrate_title)
                summary = getString(R.string.pref_vibrate_summary)
                setDefaultValue(true)
            }
            feedbackCategory.addPreference(vibratePref)

            // Sound preference
            val soundPref = SwitchPreferenceCompat(context).apply {
                key = "pref_sound"
                title = getString(R.string.pref_sound_title)
                summary = getString(R.string.pref_sound_summary)
                setDefaultValue(false)
            }
            feedbackCategory.addPreference(soundPref)

            // Popup preference
            val popupPref = SwitchPreferenceCompat(context).apply {
                key = "pref_popup"
                title = getString(R.string.pref_popup_title)
                summary = getString(R.string.pref_popup_summary)
                setDefaultValue(true)
            }
            feedbackCategory.addPreference(popupPref)

            // Vedic svara settings ship with the Sanskrit flavor only
            if (BuildConfig.SUPPORTS_VEDIC_SVARA) {
                // Vedic category - svara encoding and the font that renders it
                val vedicCategory = PreferenceCategory(context).apply {
                    title = getString(R.string.pref_category_vedic)
                    key = "vedic_category"
                }
                screen.addPreference(vedicCategory)

                val svaraEncodingPref = ListPreference(context).apply {
                    key = "pref_svara_encoding"
                    title = getString(R.string.pref_svara_encoding_title)
                    summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
                    entries = resources.getStringArray(R.array.svara_encoding_entries)
                    entryValues = resources.getStringArray(R.array.svara_encoding_values)
                    setDefaultValue(SvaraEncoding.DEFAULT.prefValue)
                    isIconSpaceReserved = false
                }
                vedicCategory.addPreference(svaraEncodingPref)

                // Changing the encoding moves the display font with it, matching the
                // on-keyboard panel - neither font can render the other's marks
                svaraEncodingPref.setOnPreferenceChangeListener { _, newValue ->
                    val encoding = SvaraEncoding.fromPrefValue(newValue as? String)
                    // Setting the value on the preference persists it too
                    findPreference<ListPreference>("pref_font")?.value = encoding.fontAsset
                    true
                }

                val downloadFontPref = Preference(context).apply {
                    key = "pref_download_vijayadv"
                    title = getString(R.string.download_vijayadv_font)
                    summary = getString(R.string.download_vijayadv_font_summary)
                    isIconSpaceReserved = false
                    setOnPreferenceClickListener {
                        when (val result = FontExporter.exportVijayaDv(context)) {
                            is FontExporter.Result.SavedToDownloads ->
                                toast(getString(R.string.font_saved_to_downloads))
                            is FontExporter.Result.Shared -> Unit
                            is FontExporter.Result.Failed ->
                                toast(getString(R.string.font_export_failed, result.cause.message ?: ""))
                        }
                        true
                    }
                }
                vedicCategory.addPreference(downloadFontPref)
            }

            // Appearance category
            val appearanceCategory = PreferenceCategory(context).apply {
                title = getString(R.string.pref_category_appearance)
                key = "appearance_category"
            }
            screen.addPreference(appearanceCategory)

            // Font selection (moved to top of appearance)
            val fontEntries = resources.getStringArray(R.array.font_entries)
            val fontValues = resources.getStringArray(R.array.font_values)
            Log.d("ImeSettings", "Font entries: ${fontEntries.toList()}, values: ${fontValues.toList()}")
            val fontPref = ListPreference(context).apply {
                key = "pref_font"
                title = getString(R.string.pref_font_title)
                summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
                entries = fontEntries
                entryValues = fontValues
                setDefaultValue(fontValues.firstOrNull() ?: "system")
                isIconSpaceReserved = false
            }
            appearanceCategory.addPreference(fontPref)
            Log.d("ImeSettings", "Font preference added: ${fontPref.title}")

            // Long press delay
            val longPressPref = SeekBarPreference(context).apply {
                key = "pref_longpress_delay"
                title = getString(R.string.pref_longpress_title)
                summary = getString(R.string.pref_longpress_summary)
                min = 150
                max = 500
                setDefaultValue(250)
                showSeekBarValue = true
            }
            appearanceCategory.addPreference(longPressPref)

            // Key height
            val keyHeightPref = SeekBarPreference(context).apply {
                key = "pref_key_height"
                title = getString(R.string.pref_key_height_title)
                summary = getString(R.string.pref_key_height_summary)
                min = 40
                max = 60
                setDefaultValue(48)
                showSeekBarValue = true
            }
            appearanceCategory.addPreference(keyHeightPref)

            preferenceScreen = screen
        }

        private fun toast(message: String) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
