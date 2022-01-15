package mosh.com.jera_v1.repositories

import com.google.firebase.database.DatabaseReference
import mosh.com.jera_v1.models.Address
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.models.Order
import mosh.com.jera_v1.models.PickUpLocation
import mosh.com.jera_v1.ui.checkout.DELIVERY
import mosh.com.jera_v1.ui.checkout.PICK_UP

class OrdersRepository(private val orderRef: DatabaseReference) {

    fun addOrder(
        cart: List<CartItem>,
        userId: String,
        address: Address?,
        pickUpLocation: PickUpLocation?,
        ifSucceeded:(String?) ->Unit
    ) {

        val order = if (address != null) Order(
            userId = userId,
            cart = cart,
            deliveryType = DELIVERY,
            address = address
        )
        else Order(
                userId = userId,
                cart = cart,
                deliveryType = PICK_UP,
                pickUpLocation = pickUpLocation,
            )

        addOrderToDB(order,ifSucceeded)
    }

    private fun addOrderToDB(order: Order, ifSucceeded:(String?)->Unit) {
        orderRef.child(order.orderId).setValue(order)
            .addOnFailureListener {
                ifSucceeded(it.localizedMessage)
            }.addOnCompleteListener{
                ifSucceeded(null)
            }
    }
}