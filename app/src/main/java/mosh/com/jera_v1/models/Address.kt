package mosh.com.jera_v1.models

data class Address(
    val city:String = "",
    val street:String = "",
    val houseNumber:String = "",
    val postalNumber:String = "",
    val floor:String = "-",
    val apartment:String = "-",
    val entrance:String = "-",
    val phone:String=""
)