package mosh.com.jera_v1.ui.coffee

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.Coffee
import mosh.com.jera_v1.utils.*
import java.lang.Exception

class CoffeeItemViewModel : ProductItemViewModel() {
    private val productsRepository = MyApplication.productsRepo

    private lateinit var coffee: Coffee

    /**
     * finds the product line by id and sets it as the selected product line
     */
    fun setCoffeeById(id: String) {
//        TODO change the exeption
        val temp = productsRepository.findCoffeeById(id) ?: throw Exception("no coffee found")
        coffee = temp
    }


    fun onAddToCartButtonClicked(onSuccess:()->Unit){
        addToCart(
            coffee,
             chosenGrindSize,
            onSuccess)
    }

    fun setGrindSize(index: Int) {
        chosenGrindSize = grindSizes[index]
    }

    val grindSizes = listOf("Beans", "Espresso", "Moka Pot", "Filter", "French Press")
    private var chosenGrindSize = grindSizes[0]

    val name get() = coffee.name
    val roastingLevel get() = coffee.roastingLevel
    val price get() = "${coffee.price} INS" //TODO
    val bodyRating get() = coffee.body.toFloat()
    val bitternessRating get() = coffee.bitterness.toFloat()
    val sweetnessRating get() = coffee.sweetness.toFloat()
    val acidityRating get() = coffee.acidity.toFloat()
    val countryOfOrigin get() = coffee.countryOfOrigin
    val tasteProfile get() = coffee.tasteProfile
    val description get() = coffee.description
    val stockState get() = TextResource.fromStringId(if (coffee.inStock)
        R.string.in_stock else R.string.out_of_stock)
    val imageUrl get() = coffee.imageURL

}