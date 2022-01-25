package mosh.com.jera_v1.inheritance.fragments

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import mosh.com.jera_v1.R
const val ID = "id"

//This class has functions that are commonly used in fragments.
open class FragmentWithUtils : Fragment() {
    /**
     * Hide the key board from the UI when called
     */
    protected fun hideKeyBoard() {
        ContextCompat.getSystemService(requireActivity(), InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    /**
     * Build a spinner, send the index of the item in [onClickAction]
     */
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

    /**
     * Build a dialog, when clicked on the positive button returns true in [onClick].
     * If clicked on the negative button or the dialog canceled, return false in [onClick]
     */
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

    /**
     * Return true if the internet is turned on
     */
    protected fun connectedToInternet():Boolean{
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return if (capabilities != null &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) true
        else false.also { sendToSettingsPage() }
    }

    /**
     * Open a dialog that the positive button send the user to the internet settings page
     */
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