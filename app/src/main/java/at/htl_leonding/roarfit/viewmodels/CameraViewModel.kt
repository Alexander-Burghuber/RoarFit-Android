package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.nisrulz.qreader.QRDataListener

class CameraViewModel : ViewModel(), QRDataListener {
    val qrResult: MutableLiveData<String> = MutableLiveData()

    override fun onDetected(data: String?) {
        Log.d("QReader", "Value: $data")
        qrResult.postValue(data)
    }

}
