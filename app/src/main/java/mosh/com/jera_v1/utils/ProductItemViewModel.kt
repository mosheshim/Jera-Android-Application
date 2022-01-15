package mosh.com.jera_v1.utils

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.Product

open class ProductItemViewModel: BaseViewModel() {
    private val cartRepo = MyApplication.cartRepo
    private var _quantity: Int? = 1


    /**
     * setting the quantity
     * param: quantity String
     * return: error if quantity was invalid
     */
    fun setQuantity(string: String?): TextResource? {
        _quantity = string?.toIntOrNull()
        return if (_quantity == null)
            return TextResource.fromStringId(R.string.invalid_quantity_message)
        else null
    }

    private fun validate(product: Product): Boolean {
        val messageId = when {
            _quantity == null -> R.string.invalid_quantity_message
            !product.inStock -> R.string.out_of_stock_message
            else -> return true
        }
        showToast(messageId)
        return false
    }

        protected fun addToCart(product: Product,extra:String? ,onSuccess: () -> Unit) {
            if (!validate(product)) return
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