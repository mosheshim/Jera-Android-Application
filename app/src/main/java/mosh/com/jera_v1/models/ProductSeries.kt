package mosh.com.jera_v1.models

class ProductSeries(
    val id:String = "",
    val name:String = "",
    val description:String = "",
    val prices: String = "",
    val isTeaBag:Boolean = false,
    val teas: List<Tea> = listOf(),

)