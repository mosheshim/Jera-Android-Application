package mosh.com.jera_v1.ui.forms

import android.app.Application
import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.AppUser
import mosh.com.jera_v1.ui.checkout.*
import mosh.com.jera_v1.utils.EMPTY_FIELD
import mosh.com.jera_v1.utils.UiUtils

const val REQUIRED = "Required"
const val ADDRESS_NOT_VALID = "Not valid a address"

const val PASSWORD_TO_SHORT = "Must be minimum of 8 characters"
const val NO_DIGITS_OR_LETTERS_FOUND = "Must contain digits and letters"
const val PASSWORDS_DONT_MATCH = "Passwords don't match"

const val FIRST_NAME = "fName"
const val LAST_NAME = "lName"
const val EMAIL = "email"
const val PASSWORD_1 = "password_1"
const val PASSWORD_2 = "password_2"


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = MyApplication.authRepo
    private val usersRepo = MyApplication.usersRepo
    private val appRef = getApplication<Application>()

    private val fields = mutableMapOf(
        Pair(FIRST_NAME, EMPTY_FIELD),
        Pair(LAST_NAME, EMPTY_FIELD),
        Pair(EMAIL, EMPTY_FIELD),
        Pair(PASSWORD_1, EMPTY_FIELD),
        Pair(PASSWORD_2, EMPTY_FIELD),
    )

    private fun addAppUserToDB() {
        val userID = authRepo.getCurrentUserId()
        if (!userID.isNullOrBlank()) usersRepo.addUser(
            userID, AppUser(
                email = fields[EMAIL],
                fName = fields[FIRST_NAME],
                lName = fields[LAST_NAME]
            )
        )
    }

    /**
     * call back boolean value will be true if succeeded or false if failed
     *  and string value will be the error if wont succeed, else will be null
     */
    fun register(onResult: (String?) -> Unit) {
        if (!fields.containsValue(EMPTY_FIELD)) {
            authRepo.registerNewUser(fields[EMAIL]!!, fields[PASSWORD_1]!!) {
                if (it == null) addAppUserToDB()
                onResult(it)
            }
        } else onResult(appRef.getString(R.string.missed_a_field_message))
    }

    fun saveField(editable: Editable?, field: String): String? {
        val error = validateField(editable, field)
        fields[field] = if (error == null) editable.toString() else EMPTY_FIELD
        return error
    }

    fun validateField(editable: Editable?, field: String): String? {
        val string = editable.toString()
        return if (string.isEmpty()) REQUIRED
        else when (field) {
            EMAIL -> validateEmail(string)
            PASSWORD_1 -> validatePassword1(string)
            PASSWORD_2 -> validatePassword2(string)
            else -> null
        }
    }

    private fun validateEmail(string: String): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(string).matches()) ADDRESS_NOT_VALID
        else null
    }

    private fun validatePassword1(string: String): String? {
        return if (string.length < 8) PASSWORD_TO_SHORT
        else if (!hasDigits(string) || !hasLetters(string)) NO_DIGITS_OR_LETTERS_FOUND
        else null
    }

    private fun validatePassword2(string: String): String? {
        return if (string != fields[PASSWORD_1]) PASSWORDS_DONT_MATCH
        else null
    }

    private fun hasDigits(string: String): Boolean {
        string.forEach { if (it.isDigit()) return true }
        return false
    }

    private fun hasLetters(string: String): Boolean {
        string.forEach { if (it.isLetter()) return true }
        return false
    }

}