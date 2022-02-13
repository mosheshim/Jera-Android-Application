package mosh.com.jera_v1.inheritance.viewmodels

import android.text.Editable
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.utils.TextResource

const val NOT_VALID = ""

open class FormViewModel : BaseViewModel() {
     protected val usersRepo = MyApplication.usersRepo
     protected val authRepo = MyApplication.authRepo

    protected lateinit var fields: MutableMap<String, String>


    /**
     * An abstract function that required for the saveFields function to work,
     * Default validation should be in the overridden function
     */
    protected open fun validateField(editable: Editable?, field: String): TextResource? {
        return null
    }

    /**
     * Validates a string by default rules and save it.
     * This functions is meant to be overridden and call other validation function by the [field]
     * that is given.
     */
    open fun saveField(editable: Editable?, field: String): TextResource? {
        val error = validateField(editable, field)
        fields[field] = if (error == null) editable.toString() else NOT_VALID
        return error
    }

}