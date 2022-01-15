package mosh.com.jera_v1.ui.profile

import mosh.com.jera_v1.MyApplication
import mosh.com.jera_v1.utils.BaseViewModel

class ProfileViewModel : BaseViewModel() {
    fun logout(){
        MyApplication.authRepo.logout()
    }
}