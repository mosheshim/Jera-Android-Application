package mosh.com.jera_v1.ui.forms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mosh.com.jera_v1.R
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.databinding.FragmentRegisterBinding
import mosh.com.jera_v1.utils.Listeners.Companion.onLostFocusListener

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    private lateinit var fieldsMap: List<Triple<TextInputEditText, TextInputLayout, String>>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        fieldsMap = getFieldsMap()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        binding.apply {
            inputFnameLayout.isEndIconVisible = false
            inputLnameLayout.isEndIconVisible = false
            inputRegisterEmailLayout.isEndIconVisible = false


            buttonRegister.setOnClickListener { button ->
                UiUtils.hideKeyBoard(requireActivity())
                saveAllFields()
                UiUtils.changeButtonLoadingView(textViewRegister, progressIndicator, button)
                viewModel.register {
                    if (it.isNullOrEmpty())
                        findNavController().popBackStack(R.id.navigation_login, true)
                     else UiUtils.changeButtonLoadingView(
                        textViewRegister,
                        progressIndicator,
                        button
                    )

                }
            }
        }
    }

    private fun changeFieldUI(
        field:Triple<TextInputEditText, TextInputLayout, String>, error:String?){
        field.second.error = error
        field.second.isEndIconVisible = error.isNullOrEmpty()
    }

    private fun setListeners() {
        for (field in fieldsMap) onLostFocusListener(field.first) {
            changeFieldUI (field,viewModel.validateField(field.first.text, field.third))
        }
    }

    private fun saveAllFields() {
        for (field in fieldsMap) {
            changeFieldUI (field,viewModel.saveField(field.first.text, field.third))

        }
    }

    private fun getFieldsMap(): List<Triple<TextInputEditText, TextInputLayout, String>> {
        binding.apply {
            return listOf(
                Triple(inputFname, inputFnameLayout, FIRST_NAME),
                Triple(inputLname, inputLnameLayout, LAST_NAME),
                Triple(inputRegisterEmail, inputRegisterEmailLayout, EMAIL),
                Triple(inputRegisterPassword1, inputRegisterPassword1Layout, PASSWORD_1),
                Triple(inputRegisterPassword2, inputRegisterPassword2Layout, PASSWORD_2),
            )
        }
    }

}
