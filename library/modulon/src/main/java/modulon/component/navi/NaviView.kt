package modulon.component.navi

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.updatePadding
import modulon.R
import modulon.component.cell.ComponentPaddingCell
import modulon.extensions.animation.animatorSet
import modulon.extensions.font.typefaceBold
import modulon.extensions.graphics.blendColorARGB
import modulon.extensions.graphics.createOutBoundsSelectorDrawableNoAlpha
import modulon.extensions.graphics.createRoundRectDrawable
import modulon.extensions.view.*
import modulon.extensions.viewbinder.createFrameLayout
import modulon.extensions.viewbinder.createVerticalLayout
import modulon.extensions.viewbinder.noClipping
import modulon.layout.linear.HorizontalView
import modulon.layout.stack.StackView

fun blendColor(fore: Int, back: Int): Int {
    val fgColor = Color(fore)
    val bgColor = Color(back)
    val r: Float = fgColor.red * fgColor.alpha + bgColor.red * (1 - fgColor.alpha)
    val g: Float = fgColor.green * fgColor.alpha + bgColor.green * (1 - fgColor.alpha)
    val b: Float = fgColor.blue * fgColor.alpha + bgColor.blue * (1 - fgColor.alpha)
    val result = Color(r , g, b)
    return result.toArgb()
}

fun recoverAlpha(mixed: Int, back: Int): Int {
    val bgColor = Color(back)
    val mxColor = Color(mixed)
    for (alpha in 0..255) {
        val a = alpha / 255f
        val r: Float = (mxColor.red - bgColor.red * (1 - a)) / a
        val g: Float = (mxColor.green - bgColor.green * (1 - a)) / a
        val b: Float = (mxColor.blue - bgColor.blue * (1 - a)) / a
        if (r in 0f..1f && g in 0f..1f && b in 0f..1f) {
            val fore = Color(r, g, b, a)
            return fore.toArgb()
        }
    }
    return mixed
}

fun recoverAlpha(mixed: Int, back: Int, alpha: Float): Int {
    val bgColor = Color(back)
    val mxColor = Color(mixed)
    val a = alpha
    val r: Float = (mxColor.red - bgColor.red * (1 - a)) / a
    val g: Float = (mxColor.green - bgColor.green * (1 - a)) / a
    val b: Float = (mxColor.blue - bgColor.blue * (1 - a)) / a
    val fore = Color(r, g, b, a)
    return fore.toArgb()
}

class NaviView(context: Context): StackView(context) {

    // TODO: 2022/2/21
    class TypedValueAnimator<T> : ValueAnimator()

    class Item(context: Context): ComponentPaddingCell(context) {

        private val activeComponentColor = R.color.component.contextColor()
        private val inactiveComponentColor = R.color.component_dark_gray.contextColor()
        private val backgroundComponentColor = R.color.background.contextColor()
        private val backgroundComponentLightColor = R.color.background_component_light.contextColor()

        private val containerIconBackground = createFrameLayout {
            val rec = recoverAlpha(backgroundComponentLightColor, backgroundComponentColor)
            background = createRoundRectDrawable(rec, 16.dpf)
            alpha = 0f
        }

        private val containerIcon = createFrameLayout {
            view(containerIconBackground) {
                layoutHeight = 32.dp
                layoutWidth = 64.dp
                layoutGravityFrame = Gravity.CENTER
            }
            view(iconView) {
                layoutMarginTop = 4.dp
                layoutMarginBottom = 4.dp
                layoutWidth = 24.dp
                layoutHeight = 24.dp
                layoutGravityFrame = Gravity.CENTER
            }
        }

        private val container = createVerticalLayout {
            minimumWidth = ITEM_WIDTH
            layoutWidth = MATCH_PARENT
            layoutHeight = MATCH_PARENT
            layoutGravityFrame = Gravity.CENTER

            view(containerIcon) {
                layoutMarginBottom = 4.dp
                layoutGravityLinear = Gravity.CENTER_HORIZONTAL
            }
            view(textView) {
                layoutGravityLinear = Gravity.CENTER_HORIZONTAL
            }
        }

        private val checkedBg = createOutBoundsSelectorDrawableNoAlpha(recoverAlpha(activeComponentColor, backgroundComponentColor), 64.dp)

        var isChecked = false
            set(value) {
                if (value != field) {
                    field = value
                    iconView.filterColor = if (value) activeComponentColor else inactiveComponentColor
                    textView.textSolidColor = if (value) activeComponentColor else inactiveComponentColor
//                    background = if (value) checkedBg else uncheckedBg
//                    if (!value) animator.cancel() to animatorDismiss.start()
//                    if (value) animatorDismiss.cancel() to animator.start()
                    containerIconBackground.alpha = if (value) 1f else 0f
                }
            }

        private val animator = animatorSet {
            playTogether(
                ObjectAnimator.ofFloat(containerIconBackground, SCALE_X, 0.8f, 1f),
                ObjectAnimator.ofFloat(containerIconBackground, ALPHA, 0.6f, 1f),
            )
            duration = 300L
            interpolator = DecelerateInterpolator(1f)
        }

        private val animatorDismiss = animatorSet {
            playTogether(
                ObjectAnimator.ofFloat(containerIconBackground, SCALE_X, 1f, 0.8f),
                ObjectAnimator.ofFloat(containerIconBackground, ALPHA, 1f, 0f),
            )
            duration = 300L
            interpolator = DecelerateInterpolator(1f)
        }

//        internal var isRippleChecked = false
//            set(value) {
//                field = value
//                foreground = createOutBoundsSelectorDrawable(if (value) activeColoractiveColor else inactiveComponentColor, 64.dp)
//            }

        var progress = 0f
            set(value) {
                field = value
                val blended = blendColorARGB(activeComponentColor, inactiveComponentColor, value)
                iconView.filterColor = blended
                textView.textSolidColor = blended
            }

        init {
            textView.apply {
                textSize = 14.5f
                typeface = textView.typefaceBold
                isSingleLine = true
                isAllCaps = true
            }
            isChecked = false
            setPadding(0.dp, 12.dp, 0.dp, 12.dp)
            addView(container)
//            setParams(ITEM_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT)
//        expandTouchArea(top = -16.dp, bottom = 16.dp)
            foreground = checkedBg
        }
    }

    class Spacer(context: Context): View(context)

    private val buttonSection = create<HorizontalView> {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRect(0, 0, view.width, view.height + rootWindowInsets.systemWindowInsetBottom)
            }
        }
        clipToOutline = true
        clipChildren = false
        clipToPadding = false

        updatePadding(12.dp, 0.dp, 12.dp, 0.dp)
        gravity = Gravity.CENTER
        view<Spacer> {
            layoutWidth = 0
            layoutHeight = MATCH_PARENT
            layoutWeightLinear = 0.2f
        }
    }

    companion object {
        private val ITEM_WIDTH = 72.dp
    }

    init {
        noClipping()
        viewRow(buttonSection)
        backgroundTintColor = R.color.background_component.contextColor()
    }


    fun addMenu(item: Item) {
        item.apply {
            layoutWidth = 0
            layoutWeightLinear = 2f
        }
        buttonSection.addView(item, buttonSection.childCount)
        buttonSection.view<Spacer> {
            layoutWidth = 0
            layoutHeight = MATCH_PARENT
            layoutWeightLinear = 0.2f
        }
    }

}


