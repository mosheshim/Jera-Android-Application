package mosh.com.jera_v1.ui.orders

import androidx.lifecycle.ViewModel
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.utils.BaseViewModel

class OrdersViewModel : BaseViewModel() {
    private val ordersRepo = MyApplication.ordersRepo
    val orders = ordersRepo.ordersLiveData

    init {
        ordersRepo.fetchOrders()
    }



}