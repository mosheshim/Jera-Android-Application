package mosh.com.jera_v1.ui.profile

import androidx.lifecycle.ViewModel
import mosh.com.jera_v1.MyApplication

class ProfileViewModel : ViewModel() {
    fun logout(){
        MyApplication.authRepo.logout()
    }
}