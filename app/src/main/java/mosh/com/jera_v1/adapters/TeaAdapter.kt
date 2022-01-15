package mosh.com.jera_v1.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.TeaItemBinding
import mosh.com.jera_v1.models.ProductSeries
import mosh.com.jera_v1.utils.Utils

class TeaAdapter(
    private val productSeriesList: List<ProductSeries>,
    val callback: (String) -> Unit
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productLine = productSeriesList[position]
        holder.binding.apply {
            textTeaItemName.text =
                if (productLine.teas.size == 1) productLine.teas[0].name
                else productLine.name
            textTeaItemPackage.text = Utils.getTeaBagOrBrew(productLine)
            textTeaItemPrice.text = root.context
                .getString(R.string.money_symbol_with_string,productLine.prices)
            Utils.buildPicasso(productLine.teas[0].imageURL,imageTeaItem,progressbar)
        }
        holder.itemView.setOnClickListener {
            callback(productLine.id)
        }
    }

    override fun getItemCount(): Int = productSeriesList.size


}
