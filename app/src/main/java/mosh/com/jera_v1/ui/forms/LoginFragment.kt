package mosh.com.jera_v1.ui.forms

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import mosh.com.jera_v1.R
import mosh.com.jera_v1.databinding.FragmentLoginBinding
import mosh.com.jera_v1.utils.BaseFragment
import mosh.com.jera_v1.utils.Listeners.Companion.onLostFocusListener
import mosh.com.jera_v1.utils.TextResource.Companion.asString
import mosh.com.jera_v1.utils.Utils.Companion.changeButtonLoadingView


class LoginFragment : BaseFragment<LoginViewModel>() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            onLostFocusListener(inputEmailLogin){
                inputEmailLoginLayout.error =
                    viewModel.validateEmail(inputEmailLogin.text)?.asString(resources)
            }

            buttonLogin.setOnClickListener{ button ->
                hideKeyBoard()

                changeButtonLoadingView(textViewLogin,progressIndicator,button)
                viewModel.logIn(inputEmailLogin.text, inputPasswordLogin.text){
                    hideKeyBoard()
                    if (it.isNullOrEmpty()){
//                        showToast( getString(R.string.login_successfully_message))
                        findNavController().popBackStack()
                    }else {
//                        showToast(it)
                        changeButtonLoadingView(textViewLogin,progressIndicator,button)
                    }

                }
            }

            textViewNavRegister.setOnClickListener{
                findNavController().navigate(R.id.action_login_to_register)
            }

        }
    }

}