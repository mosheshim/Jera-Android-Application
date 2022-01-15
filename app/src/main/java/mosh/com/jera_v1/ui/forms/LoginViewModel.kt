package mosh.com.jera_v1.ui.forms

import android.text.Editable
import android.util.Patterns
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.utils.BaseViewModel
import mosh.com.jera_v1.utils.TextResource
import mosh.com.jera_v1.utils.TextResource.Companion.fromStringId

class LoginViewModel() : BaseViewModel() {
    private val authRepo = MyApplication.authRepo


    fun validateEmail(editable: Editable?): TextResource? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches())
            fromStringId(R.string.email_address_not_valid)
        else null
    }

    fun logIn(emailEditable: Editable?, passwordEditable: Editable?, onResult: (String?) -> Unit) {
        val email = emailEditable.toString()
        val password = passwordEditable.toString()
        if (validateEmail(emailEditable) == null && password.isNotEmpty())
            authRepo.logIn(email, password) {
                if (it == null) showToast(R.string.register_successfully_message)
                onResult(it)
            }
        else {
            onResult("missed_a_field_message")
        }
    }

}