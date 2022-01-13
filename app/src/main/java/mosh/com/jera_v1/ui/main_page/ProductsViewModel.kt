package mosh.com.jera_v1.ui.main_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.models.Coffee
import mosh.com.jera_v1.models.ProductSeries

class ProductsViewModel: ViewModel() {
    private val productsRepo = MyApplication.productsRepo

    val productLinesLiveData: LiveData<List<ProductSeries>> = productsRepo.productSeriesLiveData

    val coffeeList: LiveData<List<Coffee>> = productsRepo.coffeeLiveData


}