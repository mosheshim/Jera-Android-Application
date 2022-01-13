package mosh.com.jera_v1.models

data class Tea(
    override val id: String = "",
    override var price: Int = 0,
    override val name: String = "",
    override val imageURL:String = "",
    override val inStock:Boolean = false,
    override val description:String= "",
    /**
     * first = grams (Int)
     * second = price (Int)
     */
    val weights:List<Weight>? = null

) :Product(
    id,
    price,
    name,
    description,
    imageURL,
    inStock
)


