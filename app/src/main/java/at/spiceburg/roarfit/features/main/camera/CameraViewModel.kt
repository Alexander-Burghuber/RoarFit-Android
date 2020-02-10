package at.spiceburg.roarfit.features.main.camera

import androidx.lifecycle.ViewModel
import github.nisrulz.qreader.QRDataListener

class CameraViewModel : ViewModel(), QRDataListener {

    // val qrResult = MutableLiveData<Equipment>()

    override fun onDetected(data: String) {
        /* fixme
        val equipment = when (data.toLowerCase(Locale.ENGLISH)) {
            "treadmill" -> Equipment.TREADMILL
            "cross_trainer" -> Equipment.CROSS_TRAINER
            "exercycle" -> Equipment.EXERCYCLE
            "leg_extension" -> Equipment.LEG_EXTENSION
            else -> null
        }
        if (equipment != null) {
            qrResult.postValue(equipment)
        }*/
    }
}
