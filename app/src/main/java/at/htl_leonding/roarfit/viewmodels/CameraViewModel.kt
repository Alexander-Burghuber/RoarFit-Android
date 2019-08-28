package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.htl_leonding.roarfit.model.Equipment
import github.nisrulz.qreader.QRDataListener
import java.util.*

class CameraViewModel : ViewModel(), QRDataListener {
    val qrLiveData = MutableLiveData<String>()
    val equipmentLiveData = MutableLiveData<Equipment>()

    override fun onDetected(data: String) {
        Log.d("QReader", "Value: $data")
        Equipment.CROSS_TRAINER.toString()
        qrLiveData.postValue(data)
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
            equipmentLiveData.postValue(equipment)
        }
    }

}
