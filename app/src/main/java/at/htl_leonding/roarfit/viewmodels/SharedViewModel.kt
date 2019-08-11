package at.htl_leonding.roarfit.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var authToken = MutableLiveData<String>()
    var customerNum = MutableLiveData<Int>()
}