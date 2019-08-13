package at.htl_leonding.roarfit.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class SharedViewModel(context: Application) : AndroidViewModel(context) {
    var authToken = MutableLiveData<String>()
    var customerNum = MutableLiveData<Int>()
}