package com.app.storyapp.ui.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.app.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var inputLayout: TextInputLayout? = null

    fun setInputLayout(layout: TextInputLayout) {
        this.inputLayout = layout
    }

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when (id) {
                    R.id.ed_login_email, R.id.ed_register_email -> validateEmail(s)
                    R.id.ed_login_password, R.id.ed_register_password -> validatePassword(s)
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun validateEmail(s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            inputLayout?.error = "Email tidak boleh kosong"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            inputLayout?.error = "Email tidak valid"
        } else {
            inputLayout?.error = null
        }
    }

    private fun validatePassword(s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            inputLayout?.error = "Password tidak boleh kosong"
        } else if (s.length < 8) {
            inputLayout?.error = "Password minimal 8 karakter"
        } else {
            inputLayout?.error = null
        }
    }
}
