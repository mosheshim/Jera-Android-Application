package mosh.com.jera_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.CoffeeItemBinding
import mosh.com.jera_v1.models.Coffee
import mosh.com.jera_v1.utils.Utils

class CoffeeAdapter(private val coffeeList: List<Coffee>,val onClick:(String)->Unit) :
    RecyclerView.Adapter<CoffeeAdapter.ViewHolder>() {

    class ViewHolder(val binding: CoffeeItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CoffeeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coffee = coffeeList[position]
            holder.binding.apply {
                textCoffeeItemName.text = coffee.name
                textItemRoastingLevel.text = coffee.roastingLevel
                textItemTasteProfile.text = coffee.tasteProfile
                textCoffeeItemPrice.text =
                    root.context.getString(R.string.money_symbol, coffee.price)
                Utils.buildPicasso(coffee.imageURL,imageCoffeeItem,progressbar)
            }
        holder.itemView.setOnClickListener {
            onClick(coffee.id)
        }
    }

    override fun getItemCount(): Int = coffeeList.size

}
