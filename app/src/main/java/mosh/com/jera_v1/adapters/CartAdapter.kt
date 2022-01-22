package mosh.com.jera_v1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.CartItemBinding
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.repositories.*
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.buildPicasso

/**
 * When the button is clicked, the [onItemMenuClicked] is called and passing the item index in the
 * [cart]
 */
class CartAdapter(val cart: List<CartItem>, val onItemMenuClicked: (View, index: Int) -> Unit) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    class ViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)


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
            imageView.buildPicasso(cartItem.imageURL, progressBar)

            textName.text = cartItem.productName
            textQuantity.text = cartItem.quantity.toString()
            textPrice.text =
                root.context.getString(R.string.money_symbol_with_string, cartItem.price.toString())

            val itemExtra = cartItem.extra
            textAdditional.text =
                if (itemExtra == null) "-"
                else if (cartItem.productId.startsWith("coffee")) getGrindSizeFromId(itemExtra)
                else root.context.getString(R.string.weight_symbol, itemExtra.toString())

            buttonDeleteMenu.setOnClickListener {
                onItemMenuClicked(it, position)
            }
        }

    }

    override fun getItemCount(): Int = cart.size

    private fun getGrindSizeFromId(id: Int): String {
        return when (id) {
            0 -> BEANS
            1 -> ESPRESSO
            2 -> MOKA_POT
            3 -> FILTER
            4 -> FRENCH_PRESS
            else -> throw IllegalArgumentException("no such id: $id")
        }
    }


}