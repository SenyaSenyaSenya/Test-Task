package com.example.testtask.util

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.testtask.R

object EditTextUtils {

    private const val DRAWABLE_RIGHT = 2

    @SuppressLint("ClickableViewAccessibility")
    fun addClearButton(editText: EditText) {
        val originalDrawable = editText.background

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(null, null, getClearIcon(editText), null)
                    editText.background = getSearchBackground(editText)
                } else {
                    editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    editText.background = originalDrawable
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        editText.setOnTouchListener(OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val clearIconWidth = editText.compoundDrawables[DRAWABLE_RIGHT]?.bounds?.width() ?: 0
                if (event.rawX >= editText.right - clearIconWidth) {
                    editText.text.clear()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun getClearIcon(editText: EditText): Drawable? {
        val clearDrawable = ContextCompat.getDrawable(editText.context, R.drawable.ic_clear)
        clearDrawable?.setBounds(0, 0, clearDrawable.intrinsicWidth, clearDrawable.intrinsicHeight)
        return clearDrawable
    }

    private fun getSearchBackground(editText: EditText): Drawable? {
        return ContextCompat.getDrawable(editText.context, R.drawable.search_background)
    }
}