package mosh.com.jera_v1.repositories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import mosh.com.jera_v1.CART_PATH
import mosh.com.jera_v1.models.Address
import mosh.com.jera_v1.models.AppUser
import mosh.com.jera_v1.models.CartItem

const val ADDRESS_PATH = "address"


class UsersRepository(
    private val usersRef: DatabaseReference,
    private val authRep: AuthRepository
) {


    /**
     * Adds the user to Firebase
     */
    fun addUser(userID: String, user: AppUser) {
        usersRef.child(userID).setValue(user)
    }

    fun getUserID(): String? {
        return authRep.getCurrentUserId()
    }


    fun getUserAddress(onFetch: (Address?) -> Unit) {
        usersRef.child(authRep.getCurrentUserId()!!).child(ADDRESS_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onFetch(snapshot.getValue(Address::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    onFetch(null)
                }

            })
    }

    fun updateAddress(address: Address) {
        usersRef.child(authRep.getCurrentUserId()!!).child(ADDRESS_PATH).setValue(address)
            .addOnCompleteListener { }
            .addOnFailureListener { }

    }

    /**
     * Checks if the user has a cart in Firebase DB and returns the cart in the callback.
     * If a cart is not found it will return null
     */
    fun getCartFromFirebase(onFetch: (List<CartItem>?) -> Unit) {
        if (authRep.isLoggedIn) {
            usersRef.child(authRep.getCurrentUserId()!!).child("cart")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        onFetch(getCardFromSnapshot(snapshot))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onFetch(null)
                    }
                })
        } else onFetch(null)
    }

    fun getCardFromSnapshot(snapshot: DataSnapshot): List<CartItem> {
        val cart = mutableListOf<CartItem>()
        if (snapshot.exists()) {
            for (item in snapshot.children) {
                cart.add(item.getValue(CartItem::class.java)!!)
            }
        }
        return cart
    }

    fun updateFirebaseCart(cart: List<CartItem>) {
        if (authRep.isLoggedIn)
            usersRef.child(authRep.getCurrentUserId()!!).child(CART_PATH).setValue(cart)
    }

    fun deleteCartFromFirebase() {
        if (authRep.isLoggedIn)
            usersRef.child(authRep.getCurrentUserId()!!).child(CART_PATH).removeValue()
    }
}

