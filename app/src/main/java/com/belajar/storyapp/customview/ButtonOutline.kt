package com.belajar.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.belajar.storyapp.R

class ButtonOutline : AppCompatButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var outlineButton: Drawable =
        ContextCompat.getDrawable(context, R.drawable.outline_btn) as Drawable
    private var textColor: Int = 0

    init {
        textColor = ContextCompat.getColor(context, R.color.dark_blue)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = outlineButton
        setTextColor(textColor)

        when (this.id) {
            R.id.btn_story -> textSize = 12F
        }

    }
}