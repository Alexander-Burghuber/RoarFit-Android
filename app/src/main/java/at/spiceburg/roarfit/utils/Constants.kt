package at.spiceburg.roarfit.utils

object Constants {
    // Request Codes
    const val PERMISSION_REQUEST_CODE_CAMERA = 42

    // Shared Preferences
    const val PREFERENCE_FILE = "at.spiceburg.roarfit.PREFERENCE_FILE"
    const val ENCRYPTED_PWD = "encrypted_pwd"
    const val INITIALIZATION_VECTOR = "initialization_vector"
    const val DONT_REMIND_BIOMETRIC = "dont_remind_biometric"
    const val USERNAME = "username"
    const val JWT = "jwt"
    const val CUSTOMER_NUM = "customer_num"

    // Biometric Crypto
    const val KEY_NAME = "ROARFIT_PWD_KEY"
    const val PROVIDER = "AndroidKeyStore"
}
