package mosh.com.jera_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.TeaItemBinding
import mosh.com.jera_v1.models.ProductSeries
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.buildPicasso

/**
 * Sends the item id to [onClick] when clicked on the item
 */
class TeaAdapter(
    private val productSeriesList: List<ProductSeries>,
    val onClick: (String) -> Unit
) : RecyclerView.Adapter<TeaAdapter.ViewHolder>() {

    class ViewHolder(val binding: TeaItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TeaItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productLine = productSeriesList[position]
        holder.binding.apply {
            //If there is only one tea in the product line it will show the tea name as item name
            textTeaItemName.text =
                if (productLine.teas.size == 1) productLine.teas[0].name
                else productLine.name

            textTeaItemPackage.text = root.context.getString(
                if(productLine.isTeaBag) R.string.tea_bag else R.string.tea_brew)

            textTeaItemPrice.text = root.context
                .getString(R.string.money_symbol_with_string,productLine.prices)

            imageTeaItem.buildPicasso(productLine.teas[0].imageURL,progressbar)
        }
        holder.itemView.setOnClickListener {
            onClick(productLine.id)
        }
    }

    override fun getItemCount(): Int = productSeriesList.size


}
