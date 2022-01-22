package mosh.com.jera_v1.utils

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
//A static class that allow converting stringRes ids to regular strings without leaking context
//This class allow full language support with MVVM pattern,
sealed class TextResource {
    companion object {
        /**
         * Convert a string to TextResource
         */
        fun fromText(text: String): TextResource = SimpleTextResource(text)

        /**
         * Convert stringRes id to TextResource
         */
        fun fromStringId(@StringRes id: Int): TextResource = IdTextResource(id)

        /**
         * Convert TextResource to a string.
         * If it's a stringRes id, [resources] is used
         */
        fun TextResource.asString(resources: Resources): String = when (this) {
            is SimpleTextResource -> this.text
            is IdTextResource -> resources.getString(this.id)
        }
    }


    private data class SimpleTextResource(val text: String) : TextResource()

    private data class IdTextResource(@StringRes val id: Int) : TextResource()


}