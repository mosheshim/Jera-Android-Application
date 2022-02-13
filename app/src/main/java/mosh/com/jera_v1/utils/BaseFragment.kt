package mosh.com.jera_v1.utils

import android.os.Bundle
import android.view.View
import android.widget.*
import mosh.com.jera_v1.utils.TextResource.Companion.asString

open class BaseFragment<T:BaseViewModel> : FragmentWithUtils() {
    protected lateinit var  viewModel: T


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Shows a toast when the view model update the livedata
        viewModel.showToastLiveData.observe(viewLifecycleOwner){
            Toast.makeText(
                requireContext(),
                it.asString(resources),
                Toast.LENGTH_LONG).show()
    }
    }
}