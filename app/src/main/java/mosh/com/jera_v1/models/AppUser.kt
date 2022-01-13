package mosh.com.jera_v1.models

data class AppUser(
    val email: String? = null,
    val fName:String? = null,
    val lName:String? = null,
    val phone:String? = null,
    val cart:List<CartItem>? = null,
    val address: Map<String, String>? = null

)