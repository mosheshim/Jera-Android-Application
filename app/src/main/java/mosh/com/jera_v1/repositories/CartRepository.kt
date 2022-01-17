package mosh.com.jera_v1.repositories


import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import mosh.com.jera_v1.dao.JeraDAO
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.models.Product

import java.util.*

class CartRepository(
    private val jeraDAO: JeraDAO,
    private val userRepo: UsersRepository
) {
    val scope = CoroutineScope(Dispatchers.IO)
    val cartLiveData: LiveData<List<CartItem>> = jeraDAO.getLiveCart()
    val totalPrice get() = getCartPrice()

    init {
        updateCart()
    }

    private fun getCartPrice():String{
        var total = 0
        cartLiveData.value?.forEach { total =+ it.price }
        return total.toString()
    }

    /**
     * The function check if the cart has already been fetched fromFirebaseDB in
     * this app lifecycle, if not it will fetch from Room first and update Firebase cart.
     * That prevents any unwanted requests and make sure that Firebase is synced
     * if the user changed the cart while not been connected,  .
     * If the cart that is saved in Room is empty, the function will try to fetch from Firebase and
     * sync Room with Firebase. After syncing with Firebase the function will stop sending GET
     * request until the user will open the app again in another time.
     */

    private fun updateCart() {
        scope.launch(Dispatchers.IO) {
            val roomCart = jeraDAO.getCart()
            if (!roomCart.isNullOrEmpty()) {
                userRepo.updateFirebaseCart(roomCart)
            } else {
                userRepo.getCartFromFirebase {
                    if (it != null) {
                        scope.launch {
                            println("launched")
                            jeraDAO.addCartItems(it)
                        }
                    }
                }
            }
        }

    }


    /**
     * Adds an item to Room and updates the Firebase DB
     * If the item is not valid (quantity is null or product is out of stock) it will return
     * the error as a string, if it went through "Added" will return and the item will be added
     */
    suspend fun addItem(product: Product, quantity: Int, extra: String?) {
        also {
            val cartItem = CartItem(
                id = UUID.randomUUID().toString(),
                productId = product.id,
                imageURL = product.imageURL,
                productName = product.name,
                quantity = quantity,
                price = product.price * quantity,
                extra = extra ?: "-"
            )
            jeraDAO.addCartItem(cartItem)
            //TODO make it delete one item and not update everything
            userRepo.updateFirebaseCart(jeraDAO.getCart())
        }
    }

    /**
     * deletes the item from Room and Firebase DB
     */
    suspend fun deleteItem(cartItem: CartItem) {
        jeraDAO.deleteItem(cartItem)
        //TODO make it delete one item and not update everything
        userRepo.updateFirebaseCart(jeraDAO.getCart())
    }

    suspend fun deleteCart() {
        jeraDAO.deleteCart()
        userRepo.deleteCartFromFirebase()
    }
}