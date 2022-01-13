package mosh.com.jera_v1.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.compose.ui.text.TextLayoutInput
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.Product
import mosh.com.jera_v1.models.Tea

class Listeners {
    companion object {
        //        TODO check if it can return null
        fun textWatcher(onChange: (String) -> Unit) =
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    onChange(s.toString())
                }
            }

        fun onLostFocusListener(
            input: TextInputEditText,
            onChange: () -> Unit) {
            input.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) { onChange() }
            }
        }

    }
}