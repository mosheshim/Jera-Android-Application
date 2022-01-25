package mosh.com.jera_v1.ui.cart

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.inheritance.viewmodels.BaseViewModel

class CartViewModel : BaseViewModel() {
    private val authRepo = MyApplication.authRepo
    private val cartRepo = MyApplication.cartRepo

    private val _cart = mutableListOf<CartItem>()
    val cart: List<CartItem> get() = _cart

    init {
        /**
         * Update local cartItems list parameter from the repository
         */
        _cart.addAll(cartRepo.cartLiveData.value ?: listOf())
    }

    /**
     * Deletes the item in [index] from the local cartItem list parameter & from
     * the sever DB and Room DB
     */
    fun deleteItem(index: Int) {
        val item = _cart[index]

        _cart.removeAt(index)
        viewModelScope.launch {
            cartRepo.deleteItem(item)
        }
    }

    /**
     * Link function to auth repository
     */
    fun isLoggedIn(): Boolean {
        return authRepo.isLoggedIn
    }

//--------------------------------------getters for the view--------------------------------------//

    val totalPrice get() = cartRepo.getCartPrice(cart).toString()
    val cartIsEmpty get() = _cart.isNullOrEmpty()

}