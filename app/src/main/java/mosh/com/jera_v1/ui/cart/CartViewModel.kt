package mosh.com.jera_v1.ui.cart

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.utils.BaseViewModel

class CartViewModel : BaseViewModel(){
    private val authRepo = MyApplication.authRepo
    private val cartRepo = MyApplication.cartRepo

    private val _cart = mutableListOf<CartItem>()
    val cart:List<CartItem> get() = _cart

    init {
        _cart.addAll(cartRepo.cartLiveData.value?: listOf())
    }
    /**
     * Calls updateUI callback when Room is synced with Firebase(if user is logged in) and the cart
     * is fetched
     */
//    fun onCartLoad(updateUI:() ->Unit){
//                    _cart = cartRepo.cartLiveData.value.toMutableList()
//                    updateUI()
//    }

    fun deleteItem(index: Int) {
        val item = _cart[index]
        _cart.removeAt(index)
        viewModelScope.launch {
            cartRepo.deleteItem(item)
        }
    }

    fun isLoggedIn(): Boolean {
       return authRepo.isLoggedIn
    }



    val price get() = cartRepo.totalPrice
    val cartIsEmpty get() = _cart.isNullOrEmpty()


}