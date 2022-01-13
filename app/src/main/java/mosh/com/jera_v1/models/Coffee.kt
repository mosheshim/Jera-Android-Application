package mosh.com.jera_v1.models

const val LIGHT = "Light roast"
const val MEDIUM = "Medium roast"
const val DARK = "Dark roast"

class Coffee(
    override val id: String = "",
    override val price: Int = 0,
    override val name: String = "",
    override val imageURL:String = "",
    override var inStock:Boolean = false,
    val countryOfOrigin: String = "",
    val roastingLevel: String = "",
    val tasteProfile: String = "",
    override val description:String= "",
    val bitterness:Int = 0,
    val sweetness:Int = 0,
    val acidity:Int = 0,
    val body:Int = 0
) : Product(
    id,
    price,
    name,
    description,
    imageURL,
    inStock
)