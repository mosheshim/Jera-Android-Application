package mosh.com.jera_v1.utils

import android.text.Editable

open class FormViewModel : BaseViewModel() {
    protected lateinit var fields: MutableMap<String, String>

    protected open fun validateField(editable: Editable?, field: String): TextResource? {
        return null
    }

    open fun saveField(editable: Editable?, field: String): TextResource? {
        val error = validateField(editable, field)
        fields[field] = if (error == null) editable.toString() else EMPTY_FIELD
        return error
    }

}