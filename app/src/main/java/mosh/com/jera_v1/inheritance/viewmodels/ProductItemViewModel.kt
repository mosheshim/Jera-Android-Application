package mosh.com.jera_v1.inheritance.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.Product
import mosh.com.jera_v1.utils.TextResource

open class ProductItemViewModel : BaseViewModel() {
    private val cartRepo = MyApplication.cartRepo
    private var _quantity: Int? = 1


    /**
     * Setting the quantity to the [string] or to null if not valid(doesn't contains legal Int)
     * Shows a toast if not valid
     */
    fun setQuantity(string: String?): TextResource? {
        _quantity = string?.toIntOrNull()
        return if (_quantity == null)
            return TextResource.fromStringId(R.string.invalid_quantity_message)
        else null
    }

    /**
     * Validates that the item is not out of stock and the quantity is not null
     * Shows a toast with the error if there is one
     */
    private fun validateBeforeAddingToCart(product: Product): Boolean {
        val messageId = when {
            _quantity == null -> R.string.invalid_quantity_message
            !product.inStock -> R.string.out_of_stock_message
            else -> return true
        }
        showToast(messageId)
        return false
    }

    /**
     * Adding the product to the server DB, call [onSuccess] if all valid
     */
    protected fun addToCart(product: Product, extra: Int?, onSuccess: () -> Unit) {
        if (!validateBeforeAddingToCart(product)) return
        viewModelScope.launch {
            cartRepo.addItem(
                product,
                _quantity!!,
                extra
            )
            onSuccess()
            showToast(R.string.added_to_cart_message)

        }
    }

}