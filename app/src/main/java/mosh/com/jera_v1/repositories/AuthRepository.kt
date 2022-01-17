package mosh.com.jera_v1.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val auth: FirebaseAuth) {

    private val _isUserLoggedIn = MutableLiveData(auth.currentUser != null)
    val isLoggedInLiveData:LiveData<Boolean> get() = _isUserLoggedIn
    val isLoggedIn:Boolean get() = _isUserLoggedIn.value?: false

    init {
        auth.addAuthStateListener {
            _isUserLoggedIn.postValue(it.currentUser != null)
        }
    }

//    TODO ia that ok?
    fun destroyListeners(){
        auth.removeAuthStateListener {}
    }

    /**
     * Adds user to Firebase. If fails, the error message will send in the call back.
     * If user added successfully null will send back
     */
    fun registerNewUser(email: String, password: String, onResult: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(null)
            }
            .addOnFailureListener  {
                onResult(it.localizedMessage ?: "Error")
            }
    }


    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * signing in existent user
     * The call back boolean is true if succeeded login in and false if not.
     * The string is the error if the action failed, notice that it will return null if succeeded
     */
    fun logIn(email: String, password: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(null)
            }
            .addOnFailureListener {
                onResult(it.localizedMessage)
            }
    }

    fun logout(){
        auth.signOut()
    }

}