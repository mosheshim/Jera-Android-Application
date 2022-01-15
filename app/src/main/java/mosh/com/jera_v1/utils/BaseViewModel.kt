package mosh.com.jera_v1.utils

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel: ViewModel() {
    private val _showToastLiveData = MutableLiveData<TextResource>()
    val showToastLiveData: LiveData<TextResource> get() = _showToastLiveData

    fun showToast(string: String) = _showToastLiveData.postValue(TextResource.fromText(string))
    fun showToast(@StringRes id:Int) = _showToastLiveData.postValue(TextResource.fromStringId(id))
}