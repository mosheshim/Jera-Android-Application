package mosh.com.jera_v1.ui.orders

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.models.Order
import mosh.com.jera_v1.inheritance.viewmodels.BaseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrdersViewModel : BaseViewModel() {
    private val ordersRepo = MyApplication.ordersRepo

    private val authRepo = MyApplication.authRepo
    private val _orders = MutableLiveData<List<Order>>()

    val orders: LiveData<List<Order>> get() = _orders
    val authStateChangeLiveData get() = authRepo.authStateChangeLiveData

    init {
        getOrders()
    }

    /**
     * Updates the Livedata when the data arrived from the server
     */
    private fun getOrders() {
        ordersRepo.fetchOrders { orders ->
            val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val ordersSorted = orders.sortedByDescending {
                LocalDate.parse(it.date, dateFormat)
            }
            _orders.postValue(
                ordersSorted.subList(0, if (ordersSorted.size > 5) 5 else ordersSorted.size)
            )
        }
    }

    val textNoOrdersFoundVisibility get() =
        if (orders.value.isNullOrEmpty()) View.VISIBLE
        else View.GONE

}