package mosh.com.jera_v1.utils

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class TextResource {
    companion object {
        fun fromText(text: String): TextResource = SimpleTextResource(text)
        fun fromStringId(@StringRes id: Int): TextResource = IdTextResource(id)
//        fun fromPlural(@PluralRes id: Int, pluralValue : Int) : TextResource = PluralTextResource(id, pluralValue)


        fun TextResource.asString(resources: Resources): String = when (this) {
            is SimpleTextResource -> this.text // smart cast
            is IdTextResource -> resources.getString(this.id) // smart cast
//        is PluralTextResource -> resources.getQuantityString(this.pluralId, this.quantity) // smart cast
        }
    }


    private data class SimpleTextResource(val text: String) : TextResource()

    private data class IdTextResource(@StringRes val id: Int) : TextResource()


//private data class PluralTextResource(
//    @PluralsRes val pluralId: Int,
//    val quantity: Int
//) : TextResource()
}