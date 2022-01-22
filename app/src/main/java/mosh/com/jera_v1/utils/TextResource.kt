package mosh.com.jera_v1.utils

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class TextResource {
    companion object {
        fun fromText(text: String): TextResource = SimpleTextResource(text)
        fun fromStringId(@StringRes id: Int): TextResource = IdTextResource(id)

        fun TextResource.asString(resources: Resources): String = when (this) {
            is SimpleTextResource -> this.text
            is IdTextResource -> resources.getString(this.id)
        }
    }


    private data class SimpleTextResource(val text: String) : TextResource()

    private data class IdTextResource(@StringRes val id: Int) : TextResource()


}