package mosh.com.jera_v1.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class FireBase {
        val root = FirebaseDatabase.getInstance().reference

        val authFB = FirebaseAuth.getInstance()

}