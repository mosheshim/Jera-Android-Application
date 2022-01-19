package mosh.com.jera_v1.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.models.Address
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.models.Order
import mosh.com.jera_v1.models.PickUpLocation
import mosh.com.jera_v1.ui.checkout.DELIVERY
import mosh.com.jera_v1.ui.checkout.PICK_UP

class OrdersRepository(private val orderRef: DatabaseReference) {
    private val authRepo = MyApplication.authRepo

    private val _ordersLiveData = MutableLiveData<List<Order>>()
    val ordersLiveData: LiveData<List<Order>> get() = _ordersLiveData


    fun addOrder(
        cart: List<CartItem>,
        userId: String,
        address: Address?,
        pickUpLocation: PickUpLocation?,
        totalPrice: Int,
        ifSucceeded: (String?) -> Unit
    ) {

        val order = if (address != null) Order(
            userId = userId,
            cart = cart,
            deliveryType = DELIVERY,
            address = address,
            totalPrice = totalPrice
        )
        else Order(
            userId = userId,
            cart = cart,
            deliveryType = PICK_UP,
            pickUpLocation = pickUpLocation,
            totalPrice = totalPrice
        )

        addOrderToDB(order, ifSucceeded)
    }

    private fun addOrderToDB(order: Order, ifSucceeded: (String?) -> Unit) {
        orderRef.child(order.orderId).setValue(order)
            .addOnFailureListener {
                ifSucceeded(it.localizedMessage)
            }.addOnCompleteListener {
                ifSucceeded(null)
            }
    }

    fun fetchOrders() {
        //TODO make const
        orderRef.orderByChild("userId")
            .equalTo(authRepo.getCurrentUserId()!!)
            .limitToFirst(5)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) //TODO make all repositories fetch like that
                    _ordersLiveData.postValue(getOrdersFromSnapshot(snapshot))
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