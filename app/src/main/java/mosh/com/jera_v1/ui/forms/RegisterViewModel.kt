package mosh.com.jera_v1.ui.forms

import android.text.Editable
import android.util.Patterns
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.AppUser
import mosh.com.jera_v1.utils.EMPTY_FIELD
import mosh.com.jera_v1.utils.BaseViewModel
import mosh.com.jera_v1.utils.FormViewModel
import mosh.com.jera_v1.utils.TextResource
import mosh.com.jera_v1.utils.TextResource.Companion.fromStringId



const val FIRST_NAME = "fName"
const val LAST_NAME = "lName"
const val EMAIL = "email"
const val PASSWORD_1 = "password_1"
const val PASSWORD_2 = "password_2"


class AuthViewModel : FormViewModel() {
    private val authRepo = MyApplication.authRepo
    private val usersRepo = MyApplication.usersRepo

    init {
        fields = mutableMapOf(
            Pair(FIRST_NAME, EMPTY_FIELD),
            Pair(LAST_NAME, EMPTY_FIELD),
            Pair(EMAIL, EMPTY_FIELD),
            Pair(PASSWORD_1, EMPTY_FIELD),
            Pair(PASSWORD_2, EMPTY_FIELD),
        )
    }

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
        } else onResult("missed_a_field_message")
    }

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

    private fun validateEmail(string: String): TextResource? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(string).matches())
            fromStringId(R.string.email_address_not_valid)
        else null
    }

    private fun validatePassword1(string: String): TextResource? {
        return when{
            string.length < 8 -> fromStringId(R.string.password_to_short)
            !hasDigits(string) || !hasLetters(string) -> fromStringId(R.string.no_digits_of_letters)
            else -> null
        }
    }

    private fun validatePassword2(string: String): TextResource? {
        return if (string != fields[PASSWORD_1]) fromStringId(R.string.passwords_do_not_match)
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