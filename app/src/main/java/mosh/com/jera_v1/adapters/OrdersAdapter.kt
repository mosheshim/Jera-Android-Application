package mosh.com.jera_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mosh.com.jera_v1.databinding.CoffeeItemBinding
import mosh.com.jera_v1.databinding.OrderItemBinding
import mosh.com.jera_v1.models.Order

class OrdersAdapter(val orders:List<Order>):
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>()  {

    class ViewHolder(val binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  {
        return ViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.apply {
            textOrderDate.text = order.date
            textTypeOfDeliveryLabel.text = order.deliveryType
            textTypeOfDelivery.text = order.address?.street ?: order.pickUpLocation?.location
            textDeliveryStatus.text = order.deliveryStatus
            textPrice.text = order.totalPrice.toString()
            textOrderId.text = order.orderId.substring(0..7)
        }
    }

    override fun getItemCount(): Int = orders.size
}