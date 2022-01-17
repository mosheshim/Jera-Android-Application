package mosh.com.jera_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mosh.com.jera_v1.databinding.ImageHolderItemBinding
import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.buildPicasso

class CheckOutImagesAdapter(private val imagesList: List<String>) :
    RecyclerView.Adapter<CheckOutImagesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ImageHolderItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ImageHolderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imagesList[position]
        holder.binding.image.buildPicasso(image,holder.binding.progressBar)
    }

    override fun getItemCount(): Int = imagesList.size

}
