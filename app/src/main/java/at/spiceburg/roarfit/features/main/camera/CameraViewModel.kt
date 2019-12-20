package at.spiceburg.roarfit.features.main.camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.spiceburg.roarfit.data.Equipment
import github.nisrulz.qreader.QRDataListener
import java.util.*

class CameraViewModel : ViewModel(), QRDataListener {

    val qrResult = MutableLiveData<Equipment>()

    override fun onDetected(data: String) {
        val equipment = when (data.toLowerCase(Locale.ENGLISH)) {
            "treadmill" -> Equipment.TREADMILL
            "cross_trainer" -> Equipment.CROSS_TRAINER
            "exercycle" -> Equipment.EXERCYCLE
            "leg_extension" -> Equipment.LEG_EXTENSION
            else -> null
        }
        if (equipment != null) {
            qrResult.postValue(equipment)
        }
    }
}
