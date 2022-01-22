package mosh.com.jera_v1.repositories


import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import mosh.com.jera_v1.dao.JeraDAO
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.models.Product

import java.util.*

const val BEANS = "Beans"
const val ESPRESSO = "Espresso"
const val MOKA_POT = "Moka Pot"
const val FILTER = "Filter"
const val FRENCH_PRESS = "French Press"

class CartRepository(
    private val jeraDAO: JeraDAO,
    private val userRepo: UsersRepository
) {
    val scope = CoroutineScope(Dispatchers.IO)
    val cartLiveData: LiveData<List<CartItem>> = jeraDAO.getLiveCart()


    init {
        fetchCart()
    }

    /**
     * Calculates the price of all the items in the cart
     */
     fun getCartPrice(cart: List<CartItem> = cartLiveData.value!!): Int {
        var total = 0
        cart.forEach { total = total.plus(it.price) }
        return total
    }

    /**
     * Fetches the cart from Room DB, if it's not empty the server DB will be synced with Room DB
     * If the cart is empty in Room DB it will try fetch the cart from the server DB
     */
    private fun fetchCart() {
        scope.launch(Dispatchers.IO) {
            val roomCart = jeraDAO.getCart()
            if (!roomCart.isNullOrEmpty()) {
                userRepo.updateFirebaseCart(roomCart)
            } else {
                userRepo.getCartFromFirebase {
                    if (it != null) {
                        scope.launch {
                            jeraDAO.addCartItems(it)
                        }
                    }
                }
            }
        }

    }


    /**
     * Adds an item to Room and syncing the server DB.
     */
    suspend fun addItem(product: Product, quantity: Int, extra: Int?) {
        also {
            val cartItem = CartItem(
                id = UUID.randomUUID().toString(),
                productId = product.id,
                imageURL = product.imageURL,
                productName = product.name,
                quantity = quantity,
                price = product.price * quantity,
                extra = extra
            )
            jeraDAO.addCartItem(cartItem)
            userRepo.updateFirebaseCart(jeraDAO.getCart())
        }
    }

    /**
     * Deletes the item from Room and the server DB
     */
    suspend fun deleteItem(cartItem: CartItem) {
        jeraDAO.deleteItem(cartItem)
        userRepo.updateFirebaseCart(jeraDAO.getCart())
    }

    /**
     * Deletes the whole cart from Room DB and the server DB
     */
    suspend fun deleteCart() {
        jeraDAO.deleteCart()
        userRepo.deleteCartFromFirebase()
    }
}