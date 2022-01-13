package mosh.com.jera_v1.ui.forms

import android.app.Application
import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.utils.UiUtils.Companion.showToast

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = MyApplication.authRepo
    private val appRef = getApplication<Application>()


    fun validateEmail(editable: Editable?): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(editable.toString()).matches()) ADDRESS_NOT_VALID
        else null
    }

    fun logIn(emailEditable: Editable?, passwordEditable: Editable?, onResult: (String?) -> Unit) {
        val email = emailEditable.toString()
        val password = passwordEditable.toString()
        if (validateEmail(emailEditable) == null && password.isNotEmpty())
            authRepo.logIn(email, password) {
                if (it == null) showToast(
                    getApplication(),
                    appRef.getString(R.string.register_successfully_message)
                )
                onResult(it)
            }
        else {
            onResult(appRef.getString(R.string.missed_a_field_message))
        }
    }

}