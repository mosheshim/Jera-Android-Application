package mosh.com.jera_v1.ui.checkout

import android.text.Editable
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.R
import mosh.com.jera_v1.models.Address
import mosh.com.jera_v1.models.CartItem
import mosh.com.jera_v1.models.PickUpLocation
import mosh.com.jera_v1.utils.*
import mosh.com.jera_v1.utils.TextResource.Companion.fromStringId
import mosh.com.jera_v1.inheritance.viewmodels.FormViewModel
import mosh.com.jera_v1.inheritance.viewmodels.NOT_VALID

const val DELIVERY = "delivery"
const val PICK_UP = "pick up"
const val CITY = "city"
const val STREET = "street"
const val HOUSE_NUMBER = "house number"
const val POSTAL_NUM = "postal number"
const val FLOOR = "floor"
const val APARTMENT = "apartment"
const val ENTRANCE = "entrance"
const val DEFAULT_ADDRESS = "default address"
const val NEW_ADDRESS = "new address"
const val PHONE = "phone"
const val PHONE_PREFIX = "phone prefix"

class CheckoutViewModel : FormViewModel() {
    private val cartRepo = MyApplication.cartRepo
    private val ordersRepo = MyApplication.ordersRepo

    init {
        /**
         * initialize the fields that are filled by the user
         */
        fields = mutableMapOf(
            Pair(CITY, NOT_VALID),
            Pair(STREET, NOT_VALID),
            Pair(HOUSE_NUMBER, NOT_VALID),
            Pair(POSTAL_NUM, NOT_VALID),
            Pair(FLOOR, NOT_VALID),
            Pair(APARTMENT, NOT_VALID),
            Pair(ENTRANCE, NOT_VALID),
            Pair(PHONE, NOT_VALID),
            Pair(PHONE_PREFIX, NOT_VALID),
        )
    }

    private lateinit var cart: List<CartItem>
    val authStateChangeLiveData: LiveData<Boolean> = authRepo.authStateChangeLiveData

    private var defaultAddress: Address? = null

    private var hasDefaultAddress: Boolean = false

    private var chosenPickupLocation: PickUpLocation? = null

    private val orderType: String
        get() {
            return when {
                pickupOrDelivery.value == PICK_UP -> PICK_UP
                newOrDefaultAddress.value == DEFAULT_ADDRESS -> DEFAULT_ADDRESS
                else -> NEW_ADDRESS
            }
        }

    private var addAddressToDefault = true

    private val _pickupOrDelivery = MutableLiveData<String>()
    val pickupOrDelivery: LiveData<String> get() = _pickupOrDelivery

    private val _newOrDefaultAddress = MutableLiveData(NEW_ADDRESS)
    val newOrDefaultAddress: LiveData<String> get() = _newOrDefaultAddress

    //-----------------------------------db communication-----------------------------------------//
    /**
     * Deletes the cart from the Room DB and the server DB and sends request the server DB
     */
    private fun updateDB(ifSucceeded: (Boolean) -> Unit) {
        val address = buildAddress()
        viewModelScope.launch {
            cartRepo.deleteCart()
            if (orderType == NEW_ADDRESS && addAddressToDefault) usersRepo.updateAddress(address!!)
            addOrder(address, ifSucceeded)

        }
    }

    /**
     * Calls the addOrder function in OrderRepository with the fields that the user filled.
     * Calls [ifSucceeded] with the answer of the DB request and shows toast with the error
     * if failed
     */
    private fun addOrder(address: Address?, ifSucceeded: (Boolean) -> Unit) {
        ordersRepo.addOrder(
            cart = cart,
            userId = usersRepo.getUserID()!!,
            address = address,
            pickUpLocation = chosenPickupLocation,
            totalPrice = cartRepo.getCartPrice()
        ) {
            showToast(
                if (it) R.string.order_confirmed
                else R.string.order_could_not_complete_message
            )
            ifSucceeded(it)
        }
    }

    /**
     * Builds an address based of what the user chose (new address or the default address).
     * If the pickup option was chosen, will return null
     */
    private fun buildAddress(): Address? {
        return when (orderType) {
            PICK_UP -> null
            DEFAULT_ADDRESS -> defaultAddress!!
            else -> Address(
                fields[CITY]!!,
                fields[STREET]!!,
                fields[HOUSE_NUMBER]!!,
                fields[POSTAL_NUM]!!,
                fields[FLOOR]!!,
                fields[APARTMENT]!!,
                fields[ENTRANCE]!!,
                "${fields[PHONE_PREFIX]}${fields[PHONE]}"
            )
        }
    }

    //--------------------------------validation and saving---------------------------------------//
    /**
     * Returns false if a field is not valid and shows a toast with the problem.
     * If all fields are valid returns true and sends request to the DB.
     * The answer of the DB request will be passed to the callback [whenDone]
     */
    fun pay(whenDone: (Boolean) -> Unit): Boolean {
        fillNotRequiredFields()
        val stringId: Int? =
            when {
                pickupOrDelivery.value == null -> R.string.no_delivery_option_chosen_message
                fields[PHONE].equals(NOT_VALID) -> R.string.phone_number_now_valid_message
                orderType == NEW_ADDRESS && fields.containsValue(NOT_VALID) ->
                    R.string.empty_field_message
                orderType == PICK_UP && chosenPickupLocation == null ->
                    R.string.no_pickup_location_chosen_message
                else -> null
            }
        if (stringId == null) updateDB(whenDone).also { return true }
        else showToast(stringId)
        return false
    }

    /**
     * Validates the [number]
     * Returns the error message if there is one, if not return null
     */
    private fun validatePhone(number: String): TextResource? {
        return when {
            number.length != 7 -> fromStringId(R.string.invalid_number)
            !number.isDigitsOnly() -> fromStringId(R.string.invalid_number)
            fields[PHONE_PREFIX].isNullOrEmpty() ->
                fromStringId(R.string.choose_phone_prefix)
            else -> null
        }
    }

    /**
     * Validates the [editable] by the [field]
     * Returns the error message if there is one, if not return null
     */
    public override fun validateField(editable: Editable?, field: String): TextResource? {
        val string = editable.toString()
        return when (field) {
            CITY, STREET, HOUSE_NUMBER, POSTAL_NUM -> if (string.isEmpty())
                fromStringId(R.string.required) else null
            PHONE -> validatePhone(string)
            else -> null
        }
    }

//   Adds validation to specific fields.
    /**
     * If field is valid, saves it to [fields]
     */
    override fun saveField(editable: Editable?, field: String): TextResource? {
        if ((field != PHONE && newOrDefaultAddress.value == DEFAULT_ADDRESS)) return null
        return super.saveField(editable, field)
    }

//  Created to work with current validations functions
    /**
     * Fills the fields that are not required.
     */
    private fun fillNotRequiredFields() {
        for (fieldKey in listOf(FLOOR, APARTMENT, ENTRANCE))
            if (fields[fieldKey] == NOT_VALID) fields[fieldKey] = "-"
    }

    //------------------------------------init functions -----------------------------------------//
    /*
    Uses callback instead of livedata to have more control over the data that is passed to the view
     */
    /**
     * Calls [onLoad] when cart is loaded
     */
    fun onCartLoad(onLoad: () -> Unit) {
        cart = cartRepo.cartLiveData.value!!
        usersRepo.getUserAddress { address ->
            defaultAddress = address
            initFields()
            onLoad()
        }
    }

    /**
     * Sets the data to for the view to use
     */
    private fun initFields() {
        if (!defaultAddress?.phone.isNullOrEmpty()) {
            fields[PHONE_PREFIX] = defaultAddress!!.phone.substring(0, 3)
            fields[PHONE] = defaultAddress!!.phone.substring(3)
        }
        hasDefaultAddress = checkIfHasDefaultAddress()
        if (hasDefaultAddress) {
            _newOrDefaultAddress.postValue(DEFAULT_ADDRESS)

        } else _newAddressContainerVisibility = VISIBLE
    }

    /**
     * Checks if the user's default address has all the required information to use
     */
    private fun checkIfHasDefaultAddress(): Boolean {
        return if (defaultAddress == null) false
        else when (NOT_VALID) {
            defaultAddress!!.city,
            defaultAddress!!.street,
            defaultAddress!!.houseNumber,
            defaultAddress!!.postalNumber -> false
            else -> true
        }
    }

    //Will be loaded from the DB server in the future
    private val _pickupLocations: List<PickUpLocation> = listOf(
        PickUpLocation("Rishon lezion", "some address"),
        PickUpLocation("Tel Aviv", "some address")
    )

    /**
     * Builds the available phone prefixes in Israel
     */
    private fun getPhonePrefixList(): List<String> {
        val prefixes = mutableListOf<String>()
        for (i in 0..9) prefixes.add("05$i")
        return prefixes
    }

    //-------------------------------------on click functions-------------------------------------//
    /**
     * Sets the pick up location
     */
    fun onPickUpLocationClicked(index: Int) {
        chosenPickupLocation = _pickupLocations[index]
    }

    /**
     * Sets the phone prefix
     */
    fun onPhonePrefixClicked(index: Int) {
        fields[PHONE_PREFIX] = getPhonePrefixList()[index]
    }

    /**
     * Setts the delivery type to delivery.
     * Showing the address container visible and the pick up container hidden
     */
    fun onDeliveryButtonClicked() {
        _addressesLayoutVisibility = VISIBLE
        _selfPickUpContainerVisibility = GONE
        _pickupOrDelivery.postValue(DELIVERY)
    }

    /**
     * Sets the delivery type to pick up.
     * Showing the pick up container visible and the address container hidden
     */
    fun onSelfPickUpButtonClicked() {
        _selfPickUpContainerVisibility = VISIBLE
        _addressesLayoutVisibility = GONE
        _pickupOrDelivery.postValue(PICK_UP)
    }

    /**
     * Sets the chosen address as the default address and makes the new address form hidden
     */
    fun onDefaultAddressClicked() {
        _newAddressContainerVisibility = GONE
        _newOrDefaultAddress.postValue(DEFAULT_ADDRESS)
    }

    /**
     * Sets the chosen address as new address and makes the new address form visible
     */
    fun onNewAddressClicked() {
        _newAddressContainerVisibility = VISIBLE
        _newOrDefaultAddress.postValue(NEW_ADDRESS)

    }

    /**
     * Sets the new address as default address when the order will be uploaded to the server.
     */
    fun onAddAddressToDefaultClicked() {
        addAddressToDefault = !addAddressToDefault
    }

    //-------------------------------- visibility manager ----------------------------------------//
    private var _selfPickUpContainerVisibility = GONE
    private var _addressesLayoutVisibility = GONE
    private var _newAddressContainerVisibility = GONE

    val selfPickUpContainerVisibility get() = _selfPickUpContainerVisibility
    val addressesLayoutVisibility get() = _addressesLayoutVisibility
    val newAddressContainerVisibility get() = _newAddressContainerVisibility
    val radioChooseAddressVisibility get() = if (hasDefaultAddress) VISIBLE else GONE
    val defaultAddressContainerVisibility get() = if (hasDefaultAddress) VISIBLE else GONE

    //-------------------------------- default address getters -----------------------------------//
    val defCity get() = defaultAddress?.city
    val defStreetAndNumber get() = "${defaultAddress?.street}${defaultAddress?.houseNumber}"
    val defPostalNumber get() = defaultAddress?.postalNumber
    val defEntrance get() = defaultAddress?.entrance ?: "-"
    val defFloor get() = defaultAddress?.floor ?: "-"
    val defApartment get() = defaultAddress?.apartment ?: "-"

    val phoneNumber get() = fields[PHONE]
    val phoneNumberPrefix get() = fields[PHONE_PREFIX].takeIf { it != NOT_VALID } ?: "05"

    //-------------------------------- other getters ---------------------------------------------//
    val radioNewAddressText
        get() = if (newOrDefaultAddress.value == NEW_ADDRESS)
            fromStringId(R.string.use_this_address) else fromStringId(R.string.add_new_address)

    val totalItems get() = cart.size.toString()

    val images get() = cart.map { it.imageURL }

    val pickupLocations get() = _pickupLocations.map { it.location }

    val totalPrice get() = cartRepo.getCartPrice().toString()

    val prefixesList get() = getPhonePrefixList()
}
