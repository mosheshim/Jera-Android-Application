package mosh.com.jera_v1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.CartItemBinding
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.utils.Utils

class CartAdapter(val cart: List<CartItem>, val onItemMenuClicked: (View, index: Int) -> Unit) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    class ViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CartItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cart[position]
        holder.binding.apply {
            Utils.buildPicasso(cartItem.imageURL, imageView, progressBar)
            textName.text = cartItem.productName
            textQuantity.text = cartItem.quantity.toString()
            textPrice.text =
                root.context.getString(R.string.money_symbol_with_string, cartItem.price.toString())
            textAdditional.text = cartItem.extra
            buttonDeleteMenu.setOnClickListener {
                onItemMenuClicked(it, position)
            }
        }

    }

    override fun getItemCount(): Int = cart.size


}