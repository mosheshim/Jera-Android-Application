package mosh.com.jera_v1.models

import java.util.*

data class Order(
    val orderId:String = UUID.randomUUID().toString(),
    val userId:String = "",
    val cart: List<CartItem>? = null,
    val address: Address? = null,
    val pickUpLocation: PickUpLocation? = null,
    val deliveryType:String = "",
    val deliveryStatus: String = "Processing",
    val date: String = Date().toString(),
    val totalPrice: Int = 0
)