package mosh.com.jera_v1.ui.forms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.FragmentRegisterBinding
import mosh.com.jera_v1.inheritance.fragments.BaseFragment
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.utils.TextResource.Companion.asString

class RegisterFragment : BaseFragment<AuthViewModel>(), UiUtils {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
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
                hideKeyBoard()
                saveAllFields()
                if (connectedToInternet()) {
                    changeButtonLoadingView(textViewRegister, progressIndicator, button)
                    viewModel.register {
                        if (it) findNavController().popBackStack(R.id.navigation_login, true)
                        else changeButtonLoadingView(textViewRegister, progressIndicator, button)
                    }
                }
            }
        }
    }

    /**
     * Updates the field to show error ui
     */
    private fun changeFieldUI(
        field: Triple<TextInputEditText, TextInputLayout, String>, error: String?
    ) {
        field.second.error = error
        field.second.isEndIconVisible = error.isNullOrEmpty()
    }

    /**
     * Add listeners to all fields
     */
    private fun setListeners() {
        for (field in fieldsMap) onLostFocusListener(field.first) {
            changeFieldUI(
                field,
                viewModel.validateField(field.first.text, field.third)?.asString(resources)
            )
        }
    }

    /**
     * Saves all fields
     */
    private fun saveAllFields() {
        for (field in fieldsMap) {
            changeFieldUI(
                field,
                viewModel.saveField(field.first.text, field.third)?.asString(resources)
            )

        }
    }

    /**
     * Creates a map of all the fields inputs, layout and fields name
     */
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
