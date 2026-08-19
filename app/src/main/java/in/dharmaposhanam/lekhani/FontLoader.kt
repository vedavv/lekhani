package `in`.dharmaposhanam.lekhani

import android.content.Context
import android.graphics.Typeface

/**
 * Loads bundled keyboard fonts from assets, caching by asset name.
 *
 * Fonts live at `assets/fonts/<name>.ttf` and are named to match the values in
 * the flavor's `font_values` array, so a preference value maps straight to a file.
 */
object FontLoader {

    /** Preference value meaning "no custom font" - use the system typeface. */
    const val SYSTEM = "system"

    private val cache = mutableMapOf<String, Typeface>()

    /**
     * Load a font by asset name, or return null for the system font and for any
     * asset that fails to load (a missing font must not take the keyboard down).
     */
    fun load(context: Context, assetName: String?): Typeface? {
        if (assetName == null || assetName == SYSTEM) return null

        cache[assetName]?.let { return it }

        return try {
            Typeface.createFromAsset(context.assets, "fonts/$assetName.ttf")
                .also { cache[assetName] = it }
        } catch (e: RuntimeException) {
            null
        }
    }
}
