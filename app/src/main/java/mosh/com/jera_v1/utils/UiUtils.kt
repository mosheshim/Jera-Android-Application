package mosh.com.jera_v1.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.gone
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.visible

interface UiUtils {
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

    fun changeButtonLoadingView(textView: TextView, progressBar: ProgressBar, button: View) {
        if (button.isClickable) {
            textView.gone()
            progressBar.visible()
            button.isClickable = false
        } else {
            textView.visible()
            progressBar.gone()
            button.isClickable = true
        }
    }
}