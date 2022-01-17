package mosh.com.jera_v1.utils

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnSuccessListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import mosh.com.jera_v1.R
import mosh.com.jera_v1.utils.TextResource.Companion.asString

open class BaseFragment<T:BaseViewModel> : Fragment() {
    protected lateinit var  viewModel: T


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showToastLiveData.observe(viewLifecycleOwner){
            Toast.makeText(
                requireContext(),
                it.asString(resources),
                Toast.LENGTH_LONG).show()
    }
    }

    protected fun hideKeyBoard() {
        ContextCompat.getSystemService(requireActivity(), InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    protected fun buildSpinner(
        list: List<String>,
        spinner: AutoCompleteTextView,
        onClickAction: (Int) -> Unit
    ) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            list
        )
        spinner.setAdapter(adapter)
        spinner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                onClickAction(position)
            }
    }



    protected fun buildDialog(
        message: String,
        positiveBtnTxt: String,
        negativeBtnTxt: String,
        onClick: (Boolean) -> Unit
    ) {
        AlertDialog.Builder(requireContext())
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




}