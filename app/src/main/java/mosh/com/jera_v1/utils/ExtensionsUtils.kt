package mosh.com.jera_v1.utils

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ExtensionsUtils {
    companion object{
        /**
         * Extension for loading photos using piccasso
         */
        fun ImageView.buildPicasso(url:String, progressBar: ProgressBar){
            Picasso.get().load(url).into(this, object  : Callback {
                override fun onSuccess() = progressBar.gone()
                override fun onError(e: Exception?) = progressBar.gone()
            })
        }

        /**
         * Setts the visibility of the view to VISIBLE
         */
        fun View.visible() {
            this.visibility = View.VISIBLE
        }

        /**
         * Setts the visibility of the view to GONE
         */
        fun View.gone() {
            this.visibility = View.GONE
        }

    }
}
