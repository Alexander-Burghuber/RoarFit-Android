package at.spiceburg.roarfit.utils

object Constants {
    // Request Codes
    const val PERMISSION_REQUEST_CODE_CAMERA = 42

    // Shared Preferences file
    const val PREFERENCES_FILE = "at.spiceburg.roarfit.PREFERENCES_FILE"

    // Shared Preferences Keys
    const val ENCRYPTED_PWD = "encrypted_pwd"
    const val INITIALIZATION_VECTOR = "initialization_vector"
    const val DONT_REMIND_BIOMETRIC = "dont_remind_biometric"
    const val USERNAME = "username"
    const val JWT = "jwt"

    // Biometric Crypto
    const val KEY_NAME = "ROARFIT_PWD_KEY"
    const val PROVIDER = "AndroidKeyStore"

    // Notification Channel Id
    const val CHANNEL_ID = "RoarFit_Exercise_Notification"
    const val NOTIFICATION_ID = 1453
}
