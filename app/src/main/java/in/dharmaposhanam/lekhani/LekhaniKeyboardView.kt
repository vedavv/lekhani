package `in`.dharmaposhanam.lekhani

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Lekhani Keyboard View
 *
 * Generic keyboard view for all Indic language keyboards.
 * Renders the keyboard layout with:
 * - Dynamic key sizing based on screen width
 * - Long-press popup for alternate characters
 * - Landscape/portrait adaptation
 */
class LekhaniKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface KeyboardActionListener {
        fun onKeyPressed(key: String)
        fun onKeyLongPressed(key: String)
        fun onPopupKeySelected(key: String)
        fun onCursorMove(direction: Int) // -1 for left, 1 for right
    }

    private var listener: KeyboardActionListener? = null
    private var currentPage: KeyboardPage? = null
    private var currentPageIndex = 0
    private var customFont: Typeface? = null
    private var popupWindow: PopupWindow? = null
    private var popupLayout: LinearLayout? = null
    private var popupAlternates: List<String>? = null
    private var selectedPopupIndex = -1
    private val handler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null
    private var longPressConsumed = false

    // Dimensions
    private val keyGap: Int
    private val keyHeight: Int
    private val keyHeightLandscape: Int
    private val horizontalPadding: Int
    private val keyCornerRadius: Float

    // Long press delay in milliseconds
    private val longPressDelay = 250L

    // Track if we need to rebuild after attachment
    private var needsRebuildOnAttach = false

    // Spacebar swipe tracking for cursor movement
    private var spacebarSwipeStartX = 0f
    private var spacebarSwipeLastX = 0f
    private var spacebarSwiping = false
    private val swipeThreshold = 25f // pixels to move cursor by 1 position

    init {
        orientation = VERTICAL
        setBackgroundColor(ContextCompat.getColor(context, R.color.keyboard_background))

        // Load dimensions
        val res = context.resources
        keyGap = res.getDimensionPixelSize(R.dimen.key_gap)
        keyHeight = res.getDimensionPixelSize(R.dimen.key_height)
        keyHeightLandscape = res.getDimensionPixelSize(R.dimen.key_height_landscape)
        horizontalPadding = res.getDimensionPixelSize(R.dimen.keyboard_padding_horizontal)
        keyCornerRadius = res.getDimension(R.dimen.key_corner_radius)

        // Set padding
        setPadding(
            horizontalPadding,
            res.getDimensionPixelSize(R.dimen.keyboard_padding_top),
            horizontalPadding,
            res.getDimensionPixelSize(R.dimen.keyboard_padding_bottom)
        )
    }

    fun setKeyboardActionListener(listener: KeyboardActionListener) {
        this.listener = listener
    }

    fun setCustomFont(font: Typeface?) {
        this.customFont = font
        currentPage?.let { rebuildKeyboard(it) }
    }

    fun clearCustomFont() {
        this.customFont = null
        currentPage?.let { rebuildKeyboard(it) }
    }

    fun setKeyboardPage(page: KeyboardPage, pageIndex: Int) {
        currentPage = page
        currentPageIndex = pageIndex

        // If not attached to window yet, defer rebuild
        if (!isAttachedToWindow) {
            needsRebuildOnAttach = true
        } else {
            rebuildKeyboard(page)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Rebuild keyboard after attachment if needed
        if (needsRebuildOnAttach && currentPage != null) {
            needsRebuildOnAttach = false
            // Post to ensure layout params are ready
            post {
                currentPage?.let { rebuildKeyboard(it) }
            }
        }
    }

    // Use display width for calculations - keyboard is always full width
    private fun getEffectiveWidth(): Int {
        return if (width > 0) width else resources.displayMetrics.widthPixels
    }

    private fun rebuildKeyboard(page: KeyboardPage) {
        removeAllViews()

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val effectiveKeyHeight = if (isLandscape) keyHeightLandscape else keyHeight

        page.rows.forEachIndexed { rowIndex, row ->
            addView(createKeyRow(row, rowIndex, page.rows.size, effectiveKeyHeight))
        }
    }

    private fun createKeyRow(
        keys: List<String>,
        rowIndex: Int,
        totalRows: Int,
        keyHeight: Int
    ): LinearLayout {
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                if (rowIndex < totalRows - 1) {
                    bottomMargin = keyGap
                }
            }

            keys.forEachIndexed { keyIndex, key ->
                val keyView = createKeyView(key, rowIndex, keys.size, keyHeight)
                addView(keyView)

                if (keyIndex < keys.size - 1) {
                    (keyView.layoutParams as MarginLayoutParams).marginEnd = keyGap
                }
            }
        }
    }

    private fun createKeyView(
        key: String,
        rowIndex: Int,
        rowLength: Int,
        keyHeight: Int
    ): View {
        val keyWidth = calculateKeyWidth(key, rowIndex, rowLength)

        return TextView(context).apply {
            // Set text
            text = getKeyDisplayText(key)

            // Apply font
            customFont?.let { typeface = it }

            // Text styling
            setTextSize(TypedValue.COMPLEX_UNIT_SP, getKeyTextSize(key))
            setTextColor(getKeyTextColor(key))
            gravity = Gravity.CENTER
            includeFontPadding = false

            // Background
            setBackgroundResource(getKeyBackground(key))

            // Layout
            layoutParams = LinearLayout.LayoutParams(keyWidth, keyHeight)

            // Accessibility
            contentDescription = getKeyContentDescription(key)

            // Touch handling with long press support
            setOnTouchListener(createKeyTouchListener(key))
        }
    }

    private fun calculateKeyWidth(key: String, rowIndex: Int, rowLength: Int): Int {
        val screenWidth = getEffectiveWidth()
        val effectiveAvailable = screenWidth - 2 * horizontalPadding - (rowLength - 1) * keyGap

        // Bottom row - space bar and return
        if (key == SpecialKey.SPACE.display) {
            return (effectiveAvailable * 0.6).toInt()
        }
        if (key == SpecialKey.RETURN.display) {
            return (effectiveAvailable * 0.35).toInt()
        }

        // Backspace key slightly wider
        if (key == SpecialKey.BACKSPACE.display) {
            return (effectiveAvailable / rowLength * 1.2).toInt()
        }

        return effectiveAvailable / rowLength
    }

    private fun getKeyDisplayText(key: String): String {
        // Return key shows "Done"
        if (key == SpecialKey.RETURN.display) {
            return context.getString(R.string.key_done)
        }

        return key
    }

    private fun getKeyTextSize(key: String): Float {
        return when (key) {
            SpecialKey.RETURN.display -> 18f
            SpecialKey.SPACE.display -> 14f
            else -> 18f  // Reduced from 20f to fit Indic scripts better
        }
    }

    private fun getKeyTextColor(key: String): Int {
        return when (key) {
            SpecialKey.RETURN.display -> ContextCompat.getColor(context, R.color.key_done_text)
            else -> ContextCompat.getColor(context, R.color.key_text)
        }
    }

    private fun getKeyBackground(key: String): Int {
        return when (key) {
            SpecialKey.BACKSPACE.display -> R.drawable.key_special_background
            SpecialKey.SHIFT.display -> {
                if (currentPageIndex == 1) R.drawable.key_shift_active_background
                else R.drawable.key_special_background
            }
            SpecialKey.RETURN.display -> R.drawable.key_done_background
            else -> R.drawable.key_background
        }
    }

    private fun getKeyContentDescription(key: String): String {
        return when (key) {
            SpecialKey.BACKSPACE.display -> "Backspace"
            SpecialKey.SHIFT.display -> "Shift"
            SpecialKey.SPACE.display -> "Space"
            SpecialKey.RETURN.display -> "Done"
            else -> key
        }
    }

    private fun createKeyTouchListener(key: String): OnTouchListener {
        return OnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.isPressed = true
                    selectedPopupIndex = -1
                    longPressConsumed = false

                    // Initialize spacebar swipe tracking
                    if (key == SpecialKey.SPACE.display) {
                        spacebarSwipeStartX = event.x
                        spacebarSwipeLastX = event.x
                        spacebarSwiping = false
                    }

                    // Check if this key has alternates for long press popup
                    if (PopupMappings.hasAlternates(key)) {
                        longPressRunnable = Runnable {
                            longPressConsumed = true
                            showPopup(view, key)
                            listener?.onKeyLongPressed(key)
                        }
                        handler.postDelayed(longPressRunnable!!, longPressDelay)
                    }
                    // Spacebar long press - show keyboard picker (no popup)
                    else if (key == SpecialKey.SPACE.display) {
                        longPressRunnable = Runnable {
                            // Only trigger long press if not swiping
                            if (!spacebarSwiping) {
                                longPressConsumed = true
                                listener?.onKeyLongPressed(key)
                            }
                        }
                        handler.postDelayed(longPressRunnable!!, longPressDelay)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    view.isPressed = false

                    // Cancel long press if not triggered
                    longPressRunnable?.let { handler.removeCallbacks(it) }
                    longPressRunnable = null

                    // If popup is showing, select the item under finger
                    if (popupWindow?.isShowing == true) {
                        if (selectedPopupIndex >= 0 && popupAlternates != null) {
                            val selectedKey = popupAlternates!![selectedPopupIndex]
                            listener?.onPopupKeySelected(selectedKey)
                        }
                        dismissPopup()
                    } else if (!longPressConsumed && !spacebarSwiping) {
                        // Only fire key press if long press wasn't consumed and not swiping
                        listener?.onKeyPressed(key)
                    }

                    // Reset spacebar swipe state
                    spacebarSwiping = false
                    longPressConsumed = false
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    view.isPressed = false
                    longPressRunnable?.let { handler.removeCallbacks(it) }
                    longPressRunnable = null
                    spacebarSwiping = false
                    dismissPopup()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // Handle spacebar swipe for cursor movement
                    if (key == SpecialKey.SPACE.display) {
                        val deltaX = event.x - spacebarSwipeLastX
                        val totalDelta = event.x - spacebarSwipeStartX

                        // Detect if we're swiping (moved more than a small threshold)
                        if (kotlin.math.abs(totalDelta) > swipeThreshold / 2) {
                            spacebarSwiping = true
                            // Cancel long press when swiping starts
                            longPressRunnable?.let { handler.removeCallbacks(it) }
                            longPressRunnable = null
                        }

                        // Move cursor when threshold is crossed
                        if (kotlin.math.abs(deltaX) >= swipeThreshold) {
                            val direction = if (deltaX > 0) 1 else -1
                            listener?.onCursorMove(direction)
                            spacebarSwipeLastX = event.x
                        }
                    }
                    // If popup is showing, track finger position to highlight items
                    else if (popupWindow?.isShowing == true && popupLayout != null) {
                        updatePopupSelection(view, event.rawX, event.rawY)
                    } else {
                        // Check if moved outside key bounds (cancel long press)
                        if (event.x < 0 || event.x > view.width ||
                            event.y < 0 || event.y > view.height) {
                            view.isPressed = false
                            longPressRunnable?.let { handler.removeCallbacks(it) }
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun updatePopupSelection(anchorView: View, rawX: Float, rawY: Float) {
        val layout = popupLayout ?: return
        val alternates = popupAlternates ?: return

        // Get popup window location
        val popupLocation = IntArray(2)
        layout.getLocationOnScreen(popupLocation)

        // Convert raw coordinates to popup-relative coordinates
        val popupX = rawX - popupLocation[0]
        val popupY = rawY - popupLocation[1]

        // Find which item is under the finger
        var newSelectedIndex = -1
        if (popupY >= 0 && popupY <= layout.height) {
            var currentX = 0f
            for (i in 0 until layout.childCount) {
                val child = layout.getChildAt(i)
                if (popupX >= currentX && popupX < currentX + child.width) {
                    newSelectedIndex = i
                    break
                }
                currentX += child.width
            }
        }

        // Update highlighting if selection changed
        if (newSelectedIndex != selectedPopupIndex) {
            // Remove highlight from previous
            if (selectedPopupIndex >= 0 && selectedPopupIndex < layout.childCount) {
                layout.getChildAt(selectedPopupIndex).isPressed = false
                layout.getChildAt(selectedPopupIndex).setBackgroundResource(
                    android.R.attr.selectableItemBackground.let { attr ->
                        val outValue = TypedValue()
                        context.theme.resolveAttribute(attr, outValue, true)
                        outValue.resourceId
                    }
                )
            }

            // Add highlight to new
            if (newSelectedIndex >= 0 && newSelectedIndex < layout.childCount) {
                layout.getChildAt(newSelectedIndex).isPressed = true
                layout.getChildAt(newSelectedIndex).setBackgroundColor(
                    ContextCompat.getColor(context, R.color.popup_highlight)
                )
            }

            selectedPopupIndex = newSelectedIndex
        }
    }

    private fun showPopup(anchorView: View, key: String) {
        val alternates = PopupMappings.getAlternates(key) ?: return
        popupAlternates = alternates
        selectedPopupIndex = -1

        val layout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            setBackgroundResource(R.drawable.popup_background)
            elevation = resources.getDimension(R.dimen.popup_elevation)

            val hPadding = resources.getDimensionPixelSize(R.dimen.popup_padding_horizontal)
            val vPadding = resources.getDimensionPixelSize(R.dimen.popup_padding_vertical)
            setPadding(hPadding, vPadding, hPadding, vPadding)

            alternates.forEach { alt ->
                addView(createPopupItem(alt))
            }
        }
        popupLayout = layout

        popupWindow = PopupWindow(
            layout,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = resources.getDimension(R.dimen.popup_elevation)
            isOutsideTouchable = true
            setOnDismissListener { popupWindow = null }
        }

        // Position above the key
        val location = IntArray(2)
        anchorView.getLocationInWindow(location)

        layout.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val xOffset = (anchorView.width - layout.measuredWidth) / 2
        val yOffset = -layout.measuredHeight - keyHeight - 8

        popupWindow?.showAsDropDown(anchorView, xOffset, yOffset)
    }

    private fun createPopupItem(key: String): TextView {
        return TextView(context).apply {
            text = key
            customFont?.let { typeface = it }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            setTextColor(ContextCompat.getColor(context, R.color.popup_text))
            gravity = Gravity.CENTER

            val minWidth = resources.getDimensionPixelSize(R.dimen.popup_item_min_width)
            val minHeight = resources.getDimensionPixelSize(R.dimen.popup_item_min_height)
            minimumWidth = minWidth
            minimumHeight = minHeight

            val hPadding = resources.getDimensionPixelSize(R.dimen.popup_item_padding_horizontal)
            val vPadding = resources.getDimensionPixelSize(R.dimen.popup_item_padding_vertical)
            setPadding(hPadding, vPadding, hPadding, vPadding)

            setBackgroundResource(android.R.attr.selectableItemBackground.let { attr ->
                val outValue = TypedValue()
                context.theme.resolveAttribute(attr, outValue, true)
                outValue.resourceId
            })

            setOnClickListener {
                listener?.onPopupKeySelected(key)
                dismissPopup()
            }

            contentDescription = "Alternate character $key"
        }
    }

    private fun dismissPopup() {
        popupWindow?.dismiss()
        popupWindow = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dismissPopup()
        longPressRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Rebuild keyboard when size changes (rotation, etc.)
        currentPage?.let { rebuildKeyboard(it) }
    }
}
