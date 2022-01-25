package mosh.com.jera_v1.inheritance.fragments

import android.os.Bundle
import android.view.View
import android.widget.*
import mosh.com.jera_v1.utils.TextResource.Companion.asString
import mosh.com.jera_v1.inheritance.viewmodels.BaseViewModel

open class BaseFragment<T: BaseViewModel> : FragmentWithUtils() {
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