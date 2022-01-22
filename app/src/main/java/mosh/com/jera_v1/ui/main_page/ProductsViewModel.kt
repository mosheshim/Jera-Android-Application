package mosh.com.jera_v1.ui.main_page

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.models.Coffee
import mosh.com.jera_v1.models.ProductSeries

class ProductsViewModel : ViewModel() {
    private val productsRepo = MyApplication.productsRepo

    val productLinesLiveData: LiveData<List<ProductSeries>> = productsRepo.productSeriesLiveData

    val coffeeList: LiveData<List<Coffee>> = productsRepo.coffeeLiveData

    /**
     * Return the span number of the grid according to the orientation
     */
    fun getSpanNum(orientation: Int): Int {
        return when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> 2
            else -> 3
        }
    }
}