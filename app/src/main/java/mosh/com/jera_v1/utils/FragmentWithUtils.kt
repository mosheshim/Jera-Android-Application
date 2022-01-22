package mosh.com.jera_v1.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import mosh.com.jera_v1.R

open class FragmentWithUtils : Fragment() {

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

    protected fun connectedToInternet():Boolean{
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return if (capabilities != null &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) true
        else false.also { sendToSettingsPage() }
    }

    private fun sendToSettingsPage(){
        buildDialog(
            getString(R.string.no_internet_dialog),
            getString(R.string.go_to_settings),
            getString(R.string.cancel)
        ){
            if (it) startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    }
}