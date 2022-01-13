package mosh.com.jera_v1.ui.coffee

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.models.Coffee
import mosh.com.jera_v1.repositories.CartRepository
import mosh.com.jera_v1.utils.*
import java.lang.Exception

class CoffeeItemViewModel(application: Application) : AndroidViewModel(application) {
    private val cartRepo = MyApplication.cartRepo
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

    /**
     * validate the tea when the add_to_cart_button is pressed and calling addItem function is the
     * cart repository
     */
    fun addToCart(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val message = cartRepo.addItem(
                coffee,
                _quantity,
                if (chosenGrindSize == "Beans") chosenGrindSize
                else chosenGrindSize.plus(" grind")
            )
                UiUtils.showToast(getApplication(), message)
            if (message == ADDED) onSuccess()
        }
    }

    /**
     * setting the quantity
     * param: quantity String
     * return: error if quantity was invalid
     */
    fun setQuantity(string: String?): String? {
        val quantity: Int? = string?.toIntOrNull()
        var error: String? = null
        if (quantity == null) error = INVALID_QUANTITY
        _quantity = quantity
        return error
    }

    fun setGrindSize(index: Int) {
        chosenGrindSize = grindSizes[index]
    }

    val grindSizes = listOf("Beans", "Espresso", "Moka Pot", "Filter", "French Press")
    private var chosenGrindSize = grindSizes[0]
    private var _quantity: Int? = 1

    val name get() = coffee.name
    val roastingLevel get() = coffee.roastingLevel
    val price get() = "${coffee.price} INS"
    val bodyRating get() = coffee.body.toFloat()
    val bitternessRating get() = coffee.bitterness.toFloat()
    val sweetnessRating get() = coffee.sweetness.toFloat()
    val acidityRating get() = coffee.acidity.toFloat()
    val countryOfOrigin get() = coffee.countryOfOrigin
    val tasteProfile get() = coffee.tasteProfile
    val description get() = coffee.description
    val stockState get() = if (coffee.inStock) IN_STOCK else OUT_OF_STOCK
    val imageUrl get() = coffee.imageURL

}