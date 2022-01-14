package mosh.com.jera_v1

import android.app.Application
import com.google.firebase.database.DatabaseReference
import mosh.com.jera_v1.database.FireBase
import mosh.com.jera_v1.database.JeraDataBase
import mosh.com.jera_v1.repositories.*

const val PRODUCTS_PATH = "products"
const val CART_PATH = "cart"
const val USERS_PATH = "users"
const val ORDERS_PATH = "orders"

class MyApplication : Application() {
    companion object {
        private lateinit var instance: MyApplication

        private val firebase: FireBase by lazy {
            FireBase()
        }

        private val rootDB: DatabaseReference by lazy {
            firebase.root
        }

        private val JeraRoomDB: JeraDataBase by lazy {
            JeraDataBase.create(instance)
        }

        val authRepo: AuthRepository by lazy {
            AuthRepository(firebase.authFB)
        }

        val usersRepo: UsersRepository by lazy {
            UsersRepository(rootDB.child(USERS_PATH), authRepo)
        }

        val cartRepo: CartRepository by lazy {
            CartRepository(
                JeraRoomDB.JeraDao(),
                usersRepo
            )
        }

        val productsRepo: ProductsRepository by lazy {
            ProductsRepository(rootDB.child(PRODUCTS_PATH))
        }

        val ordersRepo: OrdersRepository by lazy {
            OrdersRepository(rootDB.child(ORDERS_PATH))
        }


    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }



}