package mosh.com.jera_v1.ui.forms

import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.LiveData
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.AppUser
import mosh.com.jera_v1.utils.FormViewModel
import mosh.com.jera_v1.utils.NOT_VALID
import mosh.com.jera_v1.utils.TextResource
import mosh.com.jera_v1.utils.TextResource.Companion.fromStringId



const val FIRST_NAME = "fName"
const val LAST_NAME = "lName"
const val EMAIL = "email"
const val PASSWORD_1 = "password_1"
const val PASSWORD_2 = "password_2"


class AuthViewModel : FormViewModel() {
    private var password1Holder:String =""

    init {
        /**
         * initialize the fields that are filled by the user
         */
        fields = mutableMapOf(
            Pair(FIRST_NAME, NOT_VALID),
            Pair(LAST_NAME, NOT_VALID),
            Pair(EMAIL, NOT_VALID),
            Pair(PASSWORD_1, NOT_VALID),
            Pair(PASSWORD_2, NOT_VALID),
        )
    }

    /**
     * Add the user to the server DB
     */
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
     * Check if all the fields went through validation process. If true, will add the server DB
     */
    fun register(onResult: (Boolean) -> Unit) {
        if (fields.containsValue(NOT_VALID)) {
            onResult(false)
            showToast(R.string.check_again_message)
            return
        }
            authRepo.registerNewUser(fields[EMAIL]!!, fields[PASSWORD_1]!!) {
                var message = R.string.user_exists_message
                if (it) {
                    addAppUserToDB()
                    message = R.string.register_successfully_message
                }
                onResult(it)
                showToast(message)
            }
    }

    /**
     * Validates the [editable] by the Patterns standards
     * If the validation fails returns [TextResource] with the error, else returns null
     */
    private fun validateEmail(string: String): TextResource? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(string).matches())
            fromStringId(R.string.email_address_not_valid)
        else null
    }

    /**
     * Validates the [editable] by the [field]
     * Returns the error message if there is one, if not return null
     */
    public override fun validateField(editable: Editable?, field: String): TextResource? {
        val string = editable.toString()
        return if (string.isEmpty()) fromStringId(R.string.required)
        else when (field) {
            EMAIL -> validateEmail(string)
            PASSWORD_1 -> validatePassword1(string)
            PASSWORD_2 -> validatePassword2(string)
            else -> null
        }
    }

    /**
     * Validates if the [string] has digits, letters and its longer then 8 characters.
     * If the validation fails returns [TextResource] with the error, else returns null
     */
    private fun validatePassword1(string: String): TextResource? {
        password1Holder = string
        return when{
            string.length < 8 -> fromStringId(R.string.password_to_short)
            !hasDigits(string) || !hasLetters(string) -> fromStringId(R.string.no_digits_of_letters)
            else -> null
        }
    }

    /**
     * Validates if the [string] has digits, letters and its longer then 8 characters.
     * If the validation fails returns [TextResource] with the error, else returns null
     */
    private fun validatePassword2(string: String): TextResource? {
        return if (string != password1Holder) fromStringId(R.string.passwords_do_not_match)
        else null
    }

    /**
     * Return true If the string has digits
     */
    private fun hasDigits(string: String): Boolean {
        string.forEach { if (it.isDigit()) return true }
        return false
    }


    /**
     * Return true If the string has letters
     */
    private fun hasLetters(string: String): Boolean {
        string.forEach { if (it.isLetter()) return true }
        return false
    }

}