package mosh.com.jera_v1.ui.checkout

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import mosh.com.jera_v1.utils.Listeners.Companion.onLostFocusListener
import mosh.com.jera_v1.utils.UiUtils
import mosh.com.jera_v1.utils.UiUtils.Companion.gone
import mosh.com.jera_v1.utils.UiUtils.Companion.showToast
import mosh.com.jera_v1.utils.UiUtils.Companion.visible

class CheckoutFragment : Fragment() {
    private lateinit var viewModel: CheckoutViewModel
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


        viewModel.onCartLoad { notifyWhenDataFetched() }
        binding.apply {

            buttonPay.setOnClickListener {
                UiUtils.hideKeyBoard(requireActivity())
                saveAllFields()
                buttonPay.isClickable = false
                buttonCancel.isClickable = false
                progressBar.visible()
                viewModel.pay {
                    var message = it
                    if (it.isNullOrEmpty()){
                        message = "Order succeeded!"
                        findNavController().popBackStack()
                    }else{
                        progressBar.gone()
                        buttonPay.isClickable = true
                        buttonCancel.isClickable = true
                        message = it
                    }
                    showToast(requireContext(), message)
                }
            }
            buttonCancel.setOnClickListener { findNavController().popBackStack() }
        }
    }
    private fun notifyWhenDataFetched() {
        setListeners()
        binding.apply {
            buttonPay.text =
                getString(R.string.money_symbol_with_pay_text, viewModel.price) //TODO change later
            layoutDeliveryOptionsButtons.mainContainer.visible()
            layoutHeader.textTotalItems.text = viewModel.totalItems

            inputPhone.setText(viewModel.phoneNumber)
            spinnerPhonePrefix.setText(viewModel.phoneNumberPrefix)
            UiUtils.buildSpinner(
                requireContext(),
                viewModel.prefixesList,
                spinnerPhonePrefix
            ) {
                viewModel.setPrefix(it)
            }
            layoutHeader.recyclerImages.adapter = CheckOutImagesAdapter(viewModel.images)
            layoutHeader.recyclerImages.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            //--------------------------------pick up or delivery buttons-------------------------//
            layoutDeliveryOptionsButtons.apply {
                radioButtonDelivery.setOnClickListener {
                    radioButtonSelfPickup.isChecked = false
                    viewModel.deliveryButtonClicked()
                }
                radioButtonSelfPickup.setOnClickListener {
                    radioButtonDelivery.isChecked = false
                    viewModel.selfPickUpButtonClicked()
                }
            }
            //-------------------------changes in order type observer------------------------------//
            viewModel.pickupOrDelivery.observe(viewLifecycleOwner) {
                dividerDeliveryOptions.visible() //TODO can delete from here?
                containerPhone.visible()

                //-------------------self pick up ----------------------//
                layoutSelfPickupFields.apply {
                    mainContainer.visibility = viewModel.selfPickUpContainerVisibility
                    UiUtils.buildSpinner(
                        requireContext(),
                        viewModel.pickupLocations,
                        spinnerPickupLocation
                    ) { viewModel.setPickUpLocation(it) }
                }
                //-------------------shipping layout -------------------//
                layoutAddressesOptions.apply {
                    mainContainer.visibility = viewModel.addressesLayoutVisibility

                    //----------------------------default address-----------------------------------//
                    containerDefaultAddress.visibility = viewModel.defaultAddressContainerVisibility
                    textDefCity.text = viewModel.defCity
                    textDefStreetAndNumber.text = viewModel.defStreetAndNumber
                    textDefPostalNum.text = viewModel.defPostalNumber
                    textDefBuildingInfo.text = viewModel.defEntranceFloorApt

                    radioButtonUseDefaultAddress.setOnClickListener {
                        radioButtonNewAddress.isChecked = false
                        viewModel.defaultAddressClicked()
                    }
                    radioButtonUseDefaultAddress.visibility = viewModel.radioChooseAddressVisibility

                    //-------------------------new address------------------------------/
                    radioButtonNewAddress.setOnClickListener {
                        radioButtonUseDefaultAddress.isChecked = false
                        viewModel.newAddressClicked()
                    }
                    buttonUseAsDefaultAddress.setOnClickListener {
                        viewModel.addAddressToDefaultClicked()
                    }

                    viewModel.newOrDefaultAddress.observe(viewLifecycleOwner) {

                        containerNewAddress.visibility = viewModel.newAddressContainerVisibility

                        radioButtonNewAddress.visibility = viewModel.radioChooseAddressVisibility
                        radioButtonNewAddress.text = viewModel.radioNewAddressText
                    }
                }

            }
        }
    }

    private fun setListeners() {
        for (field in fieldsMap) onLostFocusListener(field.first)
        { field.second.error = viewModel.validateField(field.first.text, field.third) }
    }

    private fun saveAllFields() {
        for (field in fieldsMap) {
            field.second.error = viewModel.saveField(field.first.text, field.third)
        }
    }

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
