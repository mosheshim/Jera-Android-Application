package mosh.com.jera_v1.utils

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.models.ProductSeries
import java.lang.Exception


class Utils {
    companion object {


        fun changeButtonLoadingView(
            textView: TextView,
            progressIndicator: CircularProgressIndicator,
            view: View
        ) {
                if (textView.visibility == View.GONE){
                    textView.visible()
                    progressIndicator.gone()
                } else {
                    textView.gone()
                    progressIndicator.visible()
                }
            view.isClickable = !view.isClickable
        }

         fun buildPicasso(
            uri: String,
            imageView: ImageView,
            progressBar: ProgressBar
        ) = Picasso.get()
            .load(uri)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    progressBar.gone()
                }

                override fun onError(e: Exception?) {
                    progressBar.gone()

                }
            })



        fun getTeaBagOrBrew(productSeries: ProductSeries): String {
            return if (productSeries.isTeaBag) "Tea Bag" else "Halita"
        }

         fun getTotalPrice(cart:List<CartItem>): Int {
            var price = 0
            cart.forEach { price += it.price }
            return price
        }

        fun View.visible() {
            this.visibility = View.VISIBLE
        }

        fun View.gone() {
            this.visibility = View.GONE
        }


    }


}