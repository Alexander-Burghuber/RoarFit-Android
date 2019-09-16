package at.spiceburg.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.spiceburg.roarfit.data.Equipment
import github.nisrulz.qreader.QRDataListener
import java.util.*

class CameraViewModel : ViewModel(), QRDataListener {
    val qrLD = MutableLiveData<String>()
    val equipmentLD = MutableLiveData<Equipment>()

    override fun onDetected(data: String) {
        Log.d("QReader", "Value: $data")
        qrLD.postValue(data)
        handleTextChange(data)
    }

    fun handleTextChange(data: String) {
        val equipment = when (data.toLowerCase(Locale.ENGLISH)) {
            "treadmill" -> Equipment.TREADMILL
            "cross_trainer" -> Equipment.CROSS_TRAINER
            "exercycle" -> Equipment.EXERCYCLE
            else -> null
        }
        if (equipment != null) {
            equipmentLD.postValue(equipment)
        }
    }
}