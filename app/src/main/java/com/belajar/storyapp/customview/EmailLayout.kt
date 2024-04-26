package com.belajar.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.belajar.storyapp.R
import com.belajar.storyapp.helper.isValidEmail

class EmailLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {


    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.let { email -> !isValidEmail(email.toString()) } == true) {
                    error = context.getString(R.string.error_email)
                    setBackgroundResource(R.drawable.text_edit_error)
                } else {
                    error = null
                    setBackgroundResource(R.drawable.text_edit)
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean = true


}