package mosh.com.jera_v1.ui.forms

import android.text.Editable
import android.util.Patterns
import mosh.com.jera_v1.R
import mosh.com.jera_v1.utils.FormViewModel
import mosh.com.jera_v1.utils.TextResource
import mosh.com.jera_v1.utils.TextResource.Companion.fromStringId

class LoginViewModel() : FormViewModel() {

    /**
     * Validates the [editable] by the Patterns standards
     * If the validation fails returns [TextResource] with the error, else returns null
    */
    fun validateEmail(editable: Editable?): TextResource? {
        return if (Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches()) null
        else fromStringId(R.string.email_address_not_valid)
    }

    /**
     * Validates [emailEditable] and [passwordEditable] and log the user if both valid.
     * If a field is not valid calls false in the call back
     */
    fun onLoginClicked(
        emailEditable: Editable?,
        passwordEditable: Editable?,
        onResult: (Boolean) -> Unit
    ) {
        val email = emailEditable.toString()
        val password = passwordEditable.toString()
        if (validateEmail(emailEditable) == null && password.isNotEmpty())
            login(email, password, onResult)
        else {
            onResult(false)
            showToast(R.string.empty_field_message)
        }
    }

    /**
     * Send a log in request to the server, shows a toast if succeeded or a field was incorrect.
     * Call true or false in the call back
     */
    private fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        authRepo.logIn(email, password) {
            showToast(
                if (it)
                    R.string.login_successfully_message
                else R.string.email_or_password_incorrect_message
            )
            onResult(false)
        }
    }

}