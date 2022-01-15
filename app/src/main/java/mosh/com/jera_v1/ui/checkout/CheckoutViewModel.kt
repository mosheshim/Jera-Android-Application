package mosh.com.jera_v1.ui.checkout

import android.app.Application
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
const val NOT_VALID = ""

class CheckoutViewModel : FormViewModel() {
    private val cartRepo = MyApplication.cartRepo
    private val userRepo = MyApplication.usersRepo
    private val ordersRepo = MyApplication.ordersRepo

    init {
        fields = mutableMapOf(
            Pair(CITY, EMPTY_FIELD),
            Pair(STREET, EMPTY_FIELD),
            Pair(HOUSE_NUMBER, EMPTY_FIELD),
            Pair(POSTAL_NUM, EMPTY_FIELD),
            Pair(FLOOR, EMPTY_FIELD),
            Pair(APARTMENT, EMPTY_FIELD),
            Pair(ENTRANCE, EMPTY_FIELD),
            Pair(PHONE, EMPTY_FIELD),
            Pair(PHONE_PREFIX, EMPTY_FIELD),
        )
    }

    private lateinit var cart: List<CartItem>
    private var defaultAddress: Address? = null

    private var hasDefaultAddress: Boolean = false

    private var chosenPickupLocation: PickUpLocation? = null

    private val orderType: String
        get() {
            return if (pickupOrDelivery.value == PICK_UP) PICK_UP
            else if (newOrDefaultAddress.value == DEFAULT_ADDRESS) DEFAULT_ADDRESS
            else NEW_ADDRESS
        }

    private var addAddressToDefault = true

    private val _pickupOrDelivery = MutableLiveData<String>()
    val pickupOrDelivery: LiveData<String> get() = _pickupOrDelivery

    private val _newOrDefaultAddress = MutableLiveData(NEW_ADDRESS)
    val newOrDefaultAddress: LiveData<String> get() = _newOrDefaultAddress

    //-----------------------------------db communication-----------------------------------------//
    private fun updateDB(onFinish: (String?) -> Unit) {
        val address = buildAddress()
        viewModelScope.launch {
            cartRepo.deleteCart()
            ordersRepo.addOrder(
                cart = cart,
                userId = userRepo.getUserID()!!,
                address = address,
                pickUpLocation = chosenPickupLocation,
                onFinish
            )
        }
        if (orderType == NEW_ADDRESS && addAddressToDefault) userRepo.updateAddress(address!!)
    }

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
    //validate the fields which needed by the user delivery choice
    fun pay(onSuccess: (String?) -> Unit): Boolean {
        fillNotRequiredFields()
        val stringId: Int? =
            when { //TODO find a better way to do it
                pickupOrDelivery.value == null -> R.string.no_delivery_option_chosen_message
                fields[PHONE].equals("") -> R.string.phone_number_now_valid_message
                pickupOrDelivery.value == PICK_UP -> if (chosenPickupLocation == null)
                    R.string.no_pickup_location_chosen_message else null
                orderType == DEFAULT_ADDRESS -> null
                fields.containsValue(EMPTY_FIELD) -> R.string.empty_required_field_message
                else -> null
            }
        if (stringId == null) updateDB(onSuccess).also { return true }
        else showToast(stringId)
        return false
    }

    private fun validatePhone(number: String): TextResource? {
        return when {
            number.length != 7 -> fromStringId(R.string.number_to_short)
            !number.isDigitsOnly() -> fromStringId(R.string.invalid_number)
            fields[PHONE_PREFIX].isNullOrEmpty() ->
                fromStringId(R.string.choose_phone_prefix)
            else -> null
        }
    }

    public override fun validateField(editable: Editable?, field: String): TextResource? {
        val string = editable.toString()
        return when (field) {
            CITY, STREET, HOUSE_NUMBER, POSTAL_NUM -> if (string.isEmpty())
                fromStringId(R.string.required) else null
            PHONE -> validatePhone(string)
            else -> null
        }
    }

    override fun saveField(editable: Editable?, field: String): TextResource? {
        if ((field != PHONE && newOrDefaultAddress.value == DEFAULT_ADDRESS)) return null
        return super.saveField(editable, field)
    }

    private fun fillNotRequiredFields() {
        for (fieldKey in listOf<String>(FLOOR, APARTMENT, ENTRANCE))
            if (fields[fieldKey] == EMPTY_FIELD) fields[fieldKey] = "-"
    }

    //------------------------------------init functions -----------------------------------------//
    fun onCartLoad(onLoad: () -> Unit) {
        viewModelScope.launch {
            cartRepo.getCart { fetchedCart ->
                userRepo.getUserAddress { address ->
                    defaultAddress = address
                    cart = fetchedCart
                    initFields()
                    onLoad()

                }
            }
        }
    }

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

    //TODO load From Data base
    private val _pickupLocations: List<PickUpLocation> = listOf(
        PickUpLocation("Rishon lezion", "the beer mall"),
        PickUpLocation("Tel Aviv", "Eban Gavirol")
    )


    private fun getPhonePrefixList(): List<String> {
        val prefixes = mutableListOf<String>()
        for (i in 0..9) prefixes.add("05$i")
        return prefixes
    }

    //-------------------------------------on click functions-------------------------------------//
    fun setPickUpLocation(index: Int) {
        chosenPickupLocation = _pickupLocations[index]
    }

    fun setPrefix(index: Int) {
        fields[PHONE_PREFIX] = getPhonePrefixList()[index]
    }

    fun deliveryButtonClicked() {
        _addressesLayoutVisibility = VISIBLE
        _selfPickUpContainerVisibility = GONE
        _pickupOrDelivery.postValue(DELIVERY)
    }

    fun selfPickUpButtonClicked() {
        _selfPickUpContainerVisibility = VISIBLE
        _addressesLayoutVisibility = GONE
        _pickupOrDelivery.postValue(PICK_UP)
    }

    fun defaultAddressClicked() {
        _newAddressContainerVisibility = GONE
        _newOrDefaultAddress.postValue(DEFAULT_ADDRESS)
    }

    fun newAddressClicked() {
        _newAddressContainerVisibility = VISIBLE
        _newOrDefaultAddress.postValue(NEW_ADDRESS)

    }

    fun addAddressToDefaultClicked() {
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

    val defEntranceFloorApt
        get() = "Entrance: ${defaultAddress?.entrance ?: "-"}" +
                "   Floor ${defaultAddress?.floor}   Apt:${defaultAddress?.apartment}"

    val phoneNumber get() = fields[PHONE]

    val phoneNumberPrefix get() = fields[PHONE_PREFIX].takeIf { it != EMPTY_FIELD } ?: "05"

    //-------------------------------- other getters ---------------------------------------------//
    val radioNewAddressText
        get() = if (newOrDefaultAddress.value == NEW_ADDRESS)
            "Use This Address" else "Add new address"

    val totalItems get() = "${cart.size} Items"

    val images get() = cart.map { it.imageURL }

    val pickupLocations get() = _pickupLocations.map { it.location }

    val price get() = Utils.getTotalPrice(cart)

    val prefixesList get() = getPhonePrefixList()
}
