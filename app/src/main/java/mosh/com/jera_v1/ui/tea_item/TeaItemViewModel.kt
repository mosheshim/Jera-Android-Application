package mosh.com.jera_v1.ui.tea_item

import android.view.View
import androidx.lifecycle.*
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.Tea
import mosh.com.jera_v1.models.ProductSeries
import mosh.com.jera_v1.models.Weight
import mosh.com.jera_v1.utils.*

import java.lang.Exception

class TeaItemViewModel() : ProductItemViewModel() {
    private val productsRepository = MyApplication.productsRepo

    private lateinit var productSeries: ProductSeries


    /**
     * Returns a copy of the the tea object with the price of the weight
     */
    private fun getTeaCopyWithPrice(tea: Tea): Tea =
        if (_weight != null) tea.copy(price = _weight!!.price) else tea

    /**
     * Finds the product line by id and sets it as the selected product line
     */
    fun setProductLineById(id: String) {
        productSeries =
            productsRepository.findProductLineById(id) ?: throw Exception("No product line found")
        initializeData()
    }

    /**
     * Check if a tea was chosen, if true, will add to cart the product
     */
    fun onAddToCartButtonClicked(onSuccess: () -> Unit) {
        if (tea.value == null) showToast(R.string.no_tea_chosen_message)
        else
        addToCart(
            getTeaCopyWithPrice(tea.value!!),
            _weight?.weight,
            onSuccess
        )
    }

    /**
     * Sets the chosen tea by [index]
     */
    fun onTeaClicked(index: Int) {
        onTeaClicked(productSeries.teas[index])
    }

    /**
     * Sets the chosen weight by [index]
     */
    fun setWeight(index: Int) {
        _weight = _weightList[index]
        _price = _weight!!.price.toString()
    }

    /**
     * Initialize the parameters
     */
    private fun initializeData() {
        _productLineDescription = productSeries.description
        _subTitle =TextResource.fromStringId(
            if(productSeries.isTeaBag) R.string.tea_bag else R.string.tea_brew)
        if (productSeries.teas.size > 1) {
            _imageURL = productSeries.teas[0].imageURL
            _price = productSeries.prices
            _name = productSeries.name
            _teaList = productSeries.teas
            _containerOptionVisibility = View.VISIBLE
        } else {
            _teaList = listOf()
            onTeaClicked(productSeries.teas[0])
        }
    }

    /**
     * Updates the parameters that the fragment observes
     */
    private fun onTeaClicked(tea: Tea) {
        _name = tea.name
        _imageURL = tea.imageURL
        _addToCartButtonText = TextResource.fromStringId(if (tea.inStock)
            R.string.add_to_cart else R.string.out_of_stock)
        _teaDescription = tea.description
        weightUiSetter(tea)
        _price = "${if (!_weightList.isNullOrEmpty()) _weightList[0].price else tea.price}"
        _tea.postValue(tea)
    }

    /**
     * Update the weight parameters
     */
    private fun weightUiSetter(tea: Tea) {
        if (!tea.weights.isNullOrEmpty()) {
            _weightList = tea.weights
            _weight = _weightList[0]
            _containerWeightVisibility = View.VISIBLE
        } else {
            _containerWeightVisibility = View.GONE
            _weightList = listOf()
            _weight = null
        }
    }

    private val _tea = MutableLiveData<Tea>()
    val tea: LiveData<Tea> get() = _tea

    private lateinit var _name: String
    private lateinit var _productLineDescription: String
    private lateinit var _price: String
    private lateinit var _imageURL: String
    private lateinit var _subTitle: TextResource
    private lateinit var _addToCartButtonText: TextResource
    private lateinit var _teaDescription: String
    private var _teaList: List<Tea> = listOf()
    private lateinit var _weightList: List<Weight>

    private var _weight: Weight? = null

    private var _containerOptionVisibility: Int = View.GONE
    private var _containerWeightVisibility = View.GONE

    val name: String get() = _name
    val productLineDescription: String get() = _productLineDescription
    val price: String get() = _price
    val imageURL: String get() = _imageURL
    val subTitle: TextResource get() = _subTitle
    val addToCartButtonText: TextResource get() = _addToCartButtonText
    val teaDescription: String get() = _teaDescription
    val teaListNames: List<String> get() = _teaList.map { it.name }
    val weightListNames: List<String> get() = _weightList.map { it.weight.toString()}
    val firstWeightName: TextResource get() = TextResource.fromText(_weight?.weight.toString())
    val containerOptionVisibility get() = _containerOptionVisibility
    val containerWeightVisibility get() = _containerWeightVisibility
    val isOneTea: Boolean get() = productSeries.teas.size == 1
}