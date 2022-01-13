package mosh.com.jera_v1.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import mosh.com.jera_v1.models.Coffee
import mosh.com.jera_v1.models.ProductSeries
import mosh.com.jera_v1.models.Tea

class ProductsRepository(private val productsRef: DatabaseReference) {
    init {
        fetchProducts()
    }

    private val _productSeriesLiveData = MutableLiveData<List<ProductSeries>>()
    val productSeriesLiveData: LiveData<List<ProductSeries>> get() = _productSeriesLiveData

    private val _coffeeLiveData = MutableLiveData<List<Coffee>>()
    val coffeeLiveData get() = _coffeeLiveData

    private fun fetchProducts() {
        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productSeriesList = mutableListOf<ProductSeries>()
                val coffeeList = mutableListOf<Coffee>()
                val productsSnap = snapshot.children
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (singleSnap in productsSnap) {
                        if (singleSnap.key.toString().startsWith("coffee")) {
                            coffeeList.add(singleSnap.getValue(Coffee::class.java)!!)
                        }
                        else productSeriesList.add(singleSnap.getValue(ProductSeries::class.java)!!)
                    }
                }
                _coffeeLiveData.postValue(coffeeList)
                _productSeriesLiveData.postValue(productSeriesList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun findCoffeeById(id: String): Coffee? = _coffeeLiveData.value!!.find { it.id == id }

    fun findProductLineById(id: String): ProductSeries? =
        _productSeriesLiveData.value?.find { it.id == id }

    fun findTeaById(id: String): Tea? {
        for (productLine in _productSeriesLiveData.value ?:
        throw Exception("_productSeriesLiveData was null")) {
            productLine.teas.forEach { if (it.id == id) return it }
        }
        return null
    }

}