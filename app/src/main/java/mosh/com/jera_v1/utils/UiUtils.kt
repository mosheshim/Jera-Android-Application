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

const val MONEY_SYMBOL = "INS"
const val NO_TEA_CHOSEN = "Choose Tea"
const val ADDED = "Added"
const val IN_STOCK = "In Stock"
const val OUT_OF_STOCK = "Out Of Stock"
const val INVALID_QUANTITY = "Invalid Quantity"
const val ERROR = "Error"
const val EMPTY_FIELD = ""



class UiUtils {
    companion object {

        fun hideKeyBoard(activity: FragmentActivity) {
            ContextCompat.getSystemService(activity, InputMethodManager::class.java)
                ?.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }

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

        fun showToast(context: Context, text: String, duration: Int = Toast.LENGTH_LONG) {
            Toast.makeText(
                context,
                text,
                duration
            ).show()
        }

        fun buildSpinner(
            context: Context,
            list: List<String>,
            spinner: AutoCompleteTextView,
            onClickAction: (Int) -> Unit
        ) {
            val adapter = ArrayAdapter(
                context,
                R.layout.spinner_item,
                list
            )
            spinner.setAdapter(adapter)
            spinner.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    onClickAction(position)
                }
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

        fun addToCartDialog(context: Context, onClick: (continueShopping:Boolean) -> Unit) =
            buildDialog(
                context,
                "Do you want to continue Shopping?",
                "Go To Cart",
                "Continue Shopping",
                onClick
            )
        fun deleteItemDialog(context: Context, onClick: (deleteClicked:Boolean) -> Unit){
            buildDialog(
                context,
                "Are you sure you want to delete this item?",
                "Delete",
                "Cancel",
                onClick
            )
        }


        fun buildDialog(
            context: Context,
            message: String,
            positiveBtnTxt: String,
            negativeBtnTxt:String,
            onClick: (Boolean) -> Unit
        ) {
            AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(positiveBtnTxt) { _, _ ->
                    onClick(true)
                }
                .setNegativeButton(negativeBtnTxt) { _, _ ->
                    onClick(false)
                }
                .setOnCancelListener {
                    onClick(false)
                }.show()
        }

        fun getSpanNum(resources: Resources): Int {
            return if
                           (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                2 else 3
        }

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