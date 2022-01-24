package mosh.com.jera_v1.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val auth: FirebaseAuth) {

    private val _authStateChangeLiveData = MutableLiveData(auth.currentUser != null)
    val authStateChangeLiveData: LiveData<Boolean> get() = _authStateChangeLiveData
    val isLoggedIn: Boolean get() = _authStateChangeLiveData.value ?: false

    init {
        auth.addAuthStateListener {
            _authStateChangeLiveData.postValue(it.currentUser != null)
        }
    }

    fun addAuthStateChangeListener(onChange: () -> Unit) {
        auth.addAuthStateListener { onChange() }
    }

    fun destroyListeners() {
        auth.removeAuthStateListener {}
    }

    /**
     * Adds user to Firebase. If fails, the error message will send in the call back.
     * If user added successfully null will send back
     */
    fun registerNewUser(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Signing in existent user
     * Send if the user logged successfully in [onResult]
     */
    fun logIn(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun logout() {
        auth.signOut()
    }

}