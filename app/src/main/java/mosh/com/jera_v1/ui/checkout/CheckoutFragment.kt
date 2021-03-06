package mosh.com.jera_v1.ui.checkout

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mosh.com.jera_v1.R
import mosh.com.jera_v1.adapters.CheckOutImagesAdapter
import mosh.com.jera_v1.databinding.FragmentCheckoutBinding
import mosh.com.jera_v1.inheritance.fragments.BaseFragment
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.utils.TextResource.Companion.asString

import mosh.com.jera_v1.utils.ExtensionsUtils.Companion.visible

class CheckoutFragment : BaseFragment<CheckoutViewModel>(), UiUtils {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var fieldsMap: List<Triple<TextInputEditText, TextInputLayout, String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        fieldsMap = getFieldsMap()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Pop the stack if the user logged out
        viewModel.authStateChangeLiveData.observe(viewLifecycleOwner) {
            if (!it) findNavController().popBackStack()
        }
        binding.apply {
            viewModel.onCartLoad {
                notifyWhenDataFetched()
            }
            /*
            If fields are valid, start progress dialog. If request passed through it will navigate
            to main screen.
            The progress dialog will be dismissed when the callback will be called
            */
            buttonPay.setOnClickListener {
                hideKeyBoard()
                saveAllFields()
                val progressDialog = ProgressDialog(requireContext())
                progressDialog.setTitle(getString(R.string.processing_order))
                progressDialog.setMessage(getString(R.string.just_a_few_seconds_please))

                if (connectedToInternet() && viewModel.pay {
                        if (it) findNavController().navigate(R.id.global_navigation_to_main)
                        progressDialog.dismiss()
                    }) progressDialog.show()
            }

            buttonCancel.setOnClickListener { findNavController().popBackStack() }
        }
    }

    /**
     * Update the Ui when the data is fetched successfully
     */
    private fun notifyWhenDataFetched() {
        setListeners()
        binding.apply {
            buttonPay.text =
                getString(R.string.money_symbol_with_pay_text, viewModel.totalPrice)
            layoutDeliveryOptionsButtons.mainContainer.visible()
            layoutHeader.textTotalItems.text = viewModel.totalItems

            inputPhone.setText(viewModel.phoneNumber)
            spinnerPhonePrefix.setText(viewModel.phoneNumberPrefix)
            buildSpinner(
                viewModel.prefixesList,
                spinnerPhonePrefix
            ) {
                viewModel.onPhonePrefixClicked(it)
            }
            layoutHeader.recyclerImages.adapter = CheckOutImagesAdapter(
                viewModel.images,
                layoutHeader.progressBar
            )
            layoutHeader.recyclerImages.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            //--------------------------------pick up or delivery buttons-------------------------//
            layoutDeliveryOptionsButtons.apply {
                radioButtonDelivery.setOnClickListener {
                    radioButtonSelfPickup.isChecked = false
                    viewModel.onDeliveryButtonClicked()
                }
                radioButtonSelfPickup.setOnClickListener {
                    radioButtonDelivery.isChecked = false
                    viewModel.onSelfPickUpButtonClicked()
                }
            }
            //-------------------------changes in order type observer------------------------------//
            viewModel.pickupOrDelivery.observe(viewLifecycleOwner) {
                containerPhone.visible()
                //-------------------self pick up ----------------------//
                layoutSelfPickupFields.apply {
                    mainContainer.visibility = viewModel.selfPickUpContainerVisibility
                    buildSpinner(
                        viewModel.pickupLocations,
                        spinnerPickupLocation
                    ) { viewModel.onPickUpLocationClicked(it) }

                    spinnerPickupLocation.setOnClickListener { hideKeyBoard() }
                }
                //-------------------shipping layout -------------------//
                layoutAddressesOptions.apply {
                    mainContainer.visibility = viewModel.addressesLayoutVisibility

                    //----------------------------default address-----------------------------------//
                    layoutDefaultAddress.apply {
                        containerDefaultAddress.visibility =
                            viewModel.defaultAddressContainerVisibility
                        textDefCity.text = viewModel.defCity
                        textDefStreetAndNumber.text = viewModel.defStreetAndNumber
                        textDefPostalNum.text = viewModel.defPostalNumber
                        textDefEntrance.text = viewModel.defEntrance
                        textDefFloor.text = viewModel.defFloor
                        textDefApartment.text = viewModel.defApartment

                        cardDefaultAddressDetails.setOnClickListener {
                            radioButtonUseDefaultAddress.callOnClick()
                            radioButtonUseDefaultAddress.isChecked = true
                        }

                        radioButtonUseDefaultAddress.setOnClickListener {
                            radioButtonNewAddress.isChecked = false
                            viewModel.onDefaultAddressClicked()
                        }
                        radioButtonUseDefaultAddress.visibility =
                            viewModel.radioChooseAddressVisibility

                        //-------------------------new address------------------------------/
                        radioButtonNewAddress.setOnClickListener {
                            radioButtonUseDefaultAddress.isChecked = false
                            viewModel.onNewAddressClicked()
                        }
                    }
                    buttonUseAsDefaultAddress.setOnClickListener {
                        viewModel.onAddAddressToDefaultClicked()
                    }

                    viewModel.newOrDefaultAddress.observe(viewLifecycleOwner) {

                        containerNewAddress.visibility = viewModel.newAddressContainerVisibility

                        radioButtonNewAddress.visibility = viewModel.radioChooseAddressVisibility
                        radioButtonNewAddress.text =
                            viewModel.radioNewAddressText.asString(resources)
                    }
                }

            }
        }
    }

    //This function prevent repetitive code

    /**
     * Add listeners to all fields
     */
    private fun setListeners() {
        for (field in fieldsMap) onLostFocusListener(field.first) {
            field.second.error =
                viewModel.validateField(field.first.text, field.third)?.asString(resources)
        }
    }

    /**
     * Saves all fields
     */
    private fun saveAllFields() {
        for (field in fieldsMap) {
            field.second.error =
                viewModel.saveField(field.first.text, field.third)?.asString(resources)
        }
    }

    /**
     * Creates a map of all the fields inputs, layout and fields name
     */
    private fun getFieldsMap(): List<Triple<TextInputEditText, TextInputLayout, String>> {
        binding.layoutAddressesOptions.apply {
            return listOf(
                Triple(inputCity, inputCityLayout, CITY),
                Triple(inputStreet, inputStreetLayout, STREET),
                Triple(inputHouseNumber, inputHouseNumberLayout, HOUSE_NUMBER),
                Triple(inputZip, inputZipLayout, POSTAL_NUM),
                Triple(inputFloor, inputFloorLayout, FLOOR),
                Triple(inputApartment, inputApartmentLayout, APARTMENT),
                Triple(inputEntrance, inputEntranceLayout, ENTRANCE),
                Triple(binding.inputPhone, binding.inputPhoneLayout, PHONE),
            )
        }
    }
}
