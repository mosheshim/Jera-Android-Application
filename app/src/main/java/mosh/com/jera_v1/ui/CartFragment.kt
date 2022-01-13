package mosh.com.jera_v1.ui

import android.content.Context
import androidx.fragment.app.Fragment
import mosh.com.jera_v1.utils.UiUtils

open class CartFragment : Fragment() {
    fun addToCartDialog(onClick: (continueShopping:Boolean) -> Unit) =
        UiUtils.buildDialog(
            requireContext(),
            "Do you want to continue Shopping?",
            "Go To Cart",
            "Continue Shopping",
            onClick
        )
}