package mosh.com.jera_v1.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.models.*
import mosh.com.jera_v1.ui.checkout.DELIVERY
import mosh.com.jera_v1.ui.checkout.PICK_UP

const val USER_ID_PATH = "userId"

class OrdersRepository(private val orderRef: DatabaseReference) {
    private val authRepo = MyApplication.authRepo
    private var orders = mutableListOf<Order>()

    init {
        authRepo.addAuthStateChangeListener { orders = mutableListOf() }
    }

    fun addOrder(
        cart: List<CartItem>,
        userId: String,
        address: Address? = null,
        pickUpLocation: PickUpLocation? = null,
        totalPrice: Int,
        ifSucceeded: (String?) -> Unit
    ) {
        addOrderToDB(
            ifSucceeded =  ifSucceeded,
            order = Order(
                userId = userId,
                cart = cart,
                deliveryType = DELIVERY,
                pickUpLocation = pickUpLocation,
                address = address,
                totalPrice = totalPrice
            )
        )
    }

    private fun addOrderToDB(order: Order, ifSucceeded: (String?) -> Unit) {
        orderRef.child(order.orderId).setValue(order)
            .addOnFailureListener {
                ifSucceeded(it.localizedMessage)
            }.addOnCompleteListener {
                ifSucceeded(null).also { orders.add(order) }
            }
    }

    fun fetchOrders(onFetch: (List<Order>) -> Unit) {
        if (orders.isNotEmpty()) onFetch(orders).also { return }
        orderRef.orderByChild(USER_ID_PATH)
            .equalTo(authRepo.getCurrentUserId())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChildren())
                        orders.addAll(getOrdersFromSnapshot(snapshot))
                    onFetch(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getOrdersFromSnapshot(snapshot: DataSnapshot): List<Order> {
        val orders = mutableListOf<Order>()
        for (order in snapshot.children) {
            orders.add(order.getValue(Order::class.java)!!)
        }
        return orders
    }
}