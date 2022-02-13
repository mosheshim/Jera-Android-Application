package mosh.com.jera_v1.ui.coffee_item

import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.Coffee
import mosh.com.jera_v1.utils.*
import mosh.com.jera_v1.inheritance.viewmodels.ProductItemViewModel

class CoffeeItemViewModel : ProductItemViewModel() {
    private val productsRepository = MyApplication.productsRepo

    private lateinit var coffee: Coffee

    /**
     * Finds the coffee by id and sets it as the selected product line
     */
    fun setCoffeeById(id: String) {
        coffee = productsRepository.findCoffeeById(id)!!
    }

    /**
     * Adds the selected item to the database
     */
    fun onAddToCartButtonClicked(onSuccess: () -> Unit) {
        addToCart(
            coffee,
            chosenGrindSize,
            onSuccess
        )
    }

    /**
     * Sets the chosen grind size
     */
    fun onGrindSizeClicked(index:Int) {
        chosenGrindSize = index
    }

    val grindSizesTextResources = listOf(
    TextResource.fromStringId(R.string.beans),
    TextResource.fromStringId(R.string.espresso),
    TextResource.fromStringId(R.string.moka_pot),
    TextResource.fromStringId(R.string.filter),
    TextResource.fromStringId(R.string.french_press),
    )

    private var chosenGrindSize:Int = 0

    val name get() = coffee.name
    val roastingLevel get() = coffee.roastingLevel
    val price get() = coffee.price.toString()
    val bodyRating get() = coffee.body.toFloat()
    val bitternessRating get() = coffee.bitterness.toFloat()
    val sweetnessRating get() = coffee.sweetness.toFloat()
    val acidityRating get() = coffee.acidity.toFloat()
    val countryOfOrigin get() = coffee.countryOfOrigin
    val tasteProfile get() = coffee.tasteProfile
    val description get() = coffee.description
    val addToCartButtonText
        get() = TextResource.fromStringId(
            if (coffee.inStock) R.string.add_to_cart else R.string.out_of_stock)
    val imageUrl get() = coffee.imageURL
}