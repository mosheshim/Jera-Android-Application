package mosh.com.jera_v1.utils

import android.text.Editable

const val NOT_VALID = ""

open class FormViewModel : BaseViewModel() {
    protected lateinit var fields: MutableMap<String, String>

    protected open fun validateField(editable: Editable?, field: String): TextResource? {
        return null
    }

    open fun saveField(editable: Editable?, field: String): TextResource? {
        val error = validateField(editable, field)
        fields[field] = if (error == null) editable.toString() else NOT_VALID
        return error
    }

}