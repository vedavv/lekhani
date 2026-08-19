package `in`.dharmaposhanam.lekhani

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Quick-settings panel shown by the gear key on the keyboard.
 *
 * Its reason for existing on the keyboard rather than only in the settings app:
 * svara encoding is a per-document decision, and leaving the text field to flip
 * it is what makes people stop flipping it. Each option previews itself in the
 * font it would actually use, so an unrenderable combination is visible before
 * it is chosen rather than after.
 */
class SvaraSettingsPanel(
    private val context: Context,
    private val callbacks: Callbacks
) {

    interface Callbacks {
        fun onEncodingSelected(encoding: SvaraEncoding)
        fun onDownloadVijayaDvFont()
        fun onOpenFullSettings()
    }

    private var window: PopupWindow? = null

    fun show(anchor: View, current: SvaraEncoding) {
        dismiss()

        val content = buildContent(current)

        content.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        window = PopupWindow(
            content,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = context.resources.getDimension(R.dimen.popup_elevation)
            isOutsideTouchable = true
            setOnDismissListener { window = null }
        }

        // Sit above the gear, nudged inward so a left-edge key does not push the
        // panel off screen
        val xOffset = dp(4)
        val yOffset = -content.measuredHeight - anchor.height - dp(4)
        window?.showAsDropDown(anchor, xOffset, yOffset)
    }

    fun dismiss() {
        window?.dismiss()
        window = null
    }

    private fun buildContent(current: SvaraEncoding): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.popup_background)
            elevation = context.resources.getDimension(R.dimen.popup_elevation)
            setPadding(dp(16), dp(14), dp(16), dp(14))

            addView(heading(context.getString(R.string.svara_encoding_title)))

            SvaraEncoding.entries.forEach { encoding ->
                addView(encodingOption(encoding, selected = encoding == current))
            }

            addView(divider())
            addView(
                actionRow(context.getString(R.string.download_vijayadv_font)) {
                    dismiss()
                    callbacks.onDownloadVijayaDvFont()
                }
            )
            addView(
                actionRow(context.getString(R.string.more_settings)) {
                    dismiss()
                    callbacks.onOpenFullSettings()
                }
            )
        }
    }

    private fun heading(text: String) = TextView(context).apply {
        this.text = text
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
        setTextColor(ContextCompat.getColor(context, R.color.key_text_secondary))
        setPadding(0, 0, 0, dp(8))
    }

    /**
     * One encoding choice: name, the codepoints it emits, and a preview rendered
     * in that encoding's own font so the marks either appear or visibly do not.
     */
    private fun encodingOption(encoding: SvaraEncoding, selected: Boolean): LinearLayout {
        val label = when (encoding) {
            SvaraEncoding.UNICODE -> context.getString(R.string.svara_encoding_unicode)
            SvaraEncoding.VIJAYADV -> context.getString(R.string.svara_encoding_vijayadv)
        }
        val detail = when (encoding) {
            SvaraEncoding.UNICODE -> context.getString(R.string.svara_encoding_unicode_detail)
            SvaraEncoding.VIJAYADV -> context.getString(R.string.svara_encoding_vijayadv_detail)
        }

        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(10), dp(10), dp(10), dp(10))
            if (selected) {
                setBackgroundColor(ContextCompat.getColor(context, R.color.popup_highlight))
            }
            isClickable = true
            contentDescription = label

            addView(
                TextView(context).apply {
                    text = if (selected) "✓" else " "
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setTextColor(ContextCompat.getColor(context, R.color.key_done_background))
                    width = dp(24)
                }
            )

            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(dp(190), ViewGroup.LayoutParams.WRAP_CONTENT)

                    addView(
                        TextView(context).apply {
                            text = label
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                            setTextColor(ContextCompat.getColor(context, R.color.popup_text))
                        }
                    )
                    addView(
                        TextView(context).apply {
                            text = detail
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                            setTextColor(ContextCompat.getColor(context, R.color.key_text_secondary))
                        }
                    )
                }
            )

            addView(preview(encoding))

            setOnClickListener {
                dismiss()
                callbacks.onEncodingSelected(encoding)
            }
        }
    }

    /** The three marks on a base vowel, drawn in the font that encoding uses. */
    private fun preview(encoding: SvaraEncoding) = TextView(context).apply {
        val base = SvaraMarks.DISPLAY_BASE
        text = when (encoding) {
            SvaraEncoding.UNICODE -> listOf(
                SvaraMarks.SVARITA_UNICODE,
                SvaraMarks.ANUDATTA_UNICODE,
                SvaraMarks.DEERGHA_SVARITA_UNICODE
            )
            SvaraEncoding.VIJAYADV -> listOf(
                SvaraMarks.SVARITA_PUA,
                SvaraMarks.ANUDATTA_PUA,
                SvaraMarks.DEERGHA_SVARITA_PUA
            )
        }.joinToString(" ") { "$base$it" }

        typeface = FontLoader.load(context, encoding.fontAsset) ?: Typeface.DEFAULT
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        setTextColor(ContextCompat.getColor(context, R.color.popup_text))
        gravity = Gravity.CENTER
        setPadding(dp(8), 0, 0, 0)
    }

    private fun divider() = View(context).apply {
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)).apply {
            topMargin = dp(8)
            bottomMargin = dp(4)
        }
        setBackgroundColor(ContextCompat.getColor(context, R.color.popup_border))
    }

    private fun actionRow(text: String, onClick: () -> Unit) = TextView(context).apply {
        this.text = text
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        setTextColor(ContextCompat.getColor(context, R.color.key_done_background))
        setPadding(dp(10), dp(10), dp(10), dp(10))
        isClickable = true
        setOnClickListener { onClick() }
    }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
