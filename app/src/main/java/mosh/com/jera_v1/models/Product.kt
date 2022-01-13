package mosh.com.jera_v1.models

 abstract class Product(
    open val id :String,
    open val price: Int,
    open val name: String,
    open val description:String,
    open val imageURL:String,
    open val inStock:Boolean
)