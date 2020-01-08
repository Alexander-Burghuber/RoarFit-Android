package at.spiceburg.roarfit.features.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_auth.*
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyStore
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class AuthActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var sp: SharedPreferences

    private lateinit var executor: Executor
    private lateinit var encryptPrompt: BiometricPrompt
    private lateinit var decryptPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        // replaces the launcher theme with the normal one
        setTheme(R.style.AppTheme_White)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // setup viewModel
        val appContainer = (application as MyApplication).appContainer
        viewModel = ViewModelProviders.of(
            this,
            AuthViewModel.Factory(appContainer.keyFitApi, appContainer.userRepository)
        ).get(AuthViewModel::class.java)

        sp = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)

        // biometric
        executor = ContextCompat.getMainExecutor(this)
        encryptPrompt = BiometricPrompt(this, executor, EncryptCallback())
        decryptPrompt = BiometricPrompt(this, executor, DecryptCallback())

        // observe the status of the login network request
        viewModel.login.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val data: LoginData = resource.data!!

                    // check if biometric authentication can be set up
                    val remindBiometric = sp.getBoolean(Constants.DONT_REMIND_BIOMETRIC, false)
                    val encryptedPwdExists = sp.contains(Constants.ENCRYPTED_PWD)
                    if (!encryptedPwdExists && !remindBiometric && checkBiometricRequirements()) {

                        // setup biometric encryption
                        val promptInfo = createEncryptPromptInfo()
                        val cipher = getCipherInstance()
                        cipher.init(Cipher.ENCRYPT_MODE, createKey())

                        // open biometric encryption prompt
                        encryptPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
                    } else {
                        // the login will be finished because it was a biometric (decrypt) or a normal login
                        finishLogin(data)
                    }
                }
                is Resource.Error -> {
                    resource.message?.let { msg ->
                        displaySnackbar(msg)
                    }
                    setLoading(false)
                }
            }
        }

        // setup login click listener
        button_auth_login.setOnClickListener {
            hideKeyboard()

            val username = input_username.text.toString()
            val password = input_password.text.toString()
            val customerNumber = input_customernumber.text.toString()

            if (!username.isBlank() && !password.isBlank() && !customerNumber.isBlank()) {
                setLoading(true)
                viewModel.login(username, password, customerNumber.toInt())
            } else {
                displaySnackbar("Please fill in all fields")
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val username = sp.getString(Constants.USERNAME, null)
        if (username != null) {
            input_username.setText(username)
        }

        val customerNum = sp.getInt(Constants.USER_ID, -1)
        if (customerNum != -1) {
            input_customernumber.setText(customerNum.toString())
        }

        // activate biometric decryption if the requirements are met
        if (username != null && customerNum != -1 && sp.contains(Constants.ENCRYPTED_PWD)) {

            val ivString: String? = sp.getString(Constants.INITIALIZATION_VECTOR, null)
            val key: Key? = getKey()
            if (ivString != null && key != null) {

                // setup biometric decryption
                val iv = Base64.decode(ivString, Base64.DEFAULT)
                val cipher = getCipherInstance()
                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
                val promptInfo = createDecryptPromptInfo(username, customerNum)

                // setup button to reopen prompt
                textinputlayout_auth_password.apply {
                    endIconDrawable = getDrawable(R.drawable.ic_fingerprint_black_24dp)
                    endIconMode = TextInputLayout.END_ICON_CUSTOM
                    endIconContentDescription = getString(R.string.auth_endicon_desc)
                    setEndIconOnClickListener {
                        decryptPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
                    }
                }

                // open biometric decryption prompt
                decryptPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            } else {
                throw RuntimeException("Parameters for Biometric Decryption are null")
            }
        }
    }

    private fun finishLogin(data: LoginData, callFromEncrypt: Boolean = false) {
        val userId: Int = data.customerNum
        val jwt: String = data.token

        viewModel.loadUser(userId, jwt).observe(this) { status ->
            when (status) {
                is Status.Success -> {
                    sp.edit()
                        .putInt(Constants.USER_ID, userId)
                        .putString(Constants.JWT, jwt)
                        .putString(Constants.USERNAME, data.username)
                        .apply()

                    setLoading(false)
                    startMainActivity()
                }
                is Status.Error -> {
                    // if the error occurred directly after setup of biometric login reset it
                    if (callFromEncrypt) {
                        sp.edit()
                            .remove(Constants.ENCRYPTED_PWD)
                            .remove(Constants.INITIALIZATION_VECTOR)
                            .apply()
                    }

                    status.message?.let {
                        displaySnackbar(it)
                    }
                    setLoading(false)
                }
            }
        }
    }

    private fun checkBiometricRequirements(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun createEncryptPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.auth_biometric_encrypt_title))
            .setDescription(getString(R.string.auth_biometric_encrypt_desc))
            .setNegativeButtonText(getString(R.string.auth_biometric_encrypt_negative_button))
            .build()
    }

    private fun createDecryptPromptInfo(
        username: String,
        customerNum: Int
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Quick login using biometrics")
            .setSubtitle("Username: $username, Customer number: $customerNum")
            .setNegativeButtonText("Close")
            .build()
    }

    private fun createKey(): SecretKey {
        val algorithm = KeyProperties.KEY_ALGORITHM_AES
        val keyGenerator = KeyGenerator.getInstance(algorithm, Constants.PROVIDER)
        val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(Constants.KEY_NAME, purposes)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun getKey(): Key? {
        val keyStore = KeyStore.getInstance(Constants.PROVIDER)
        keyStore.load(null)
        return keyStore.getKey(Constants.KEY_NAME, null)
    }

    private fun getCipherInstance(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun setLoading(isLoading: Boolean) {
        input_username.isEnabled = !isLoading
        input_password.isEnabled = !isLoading
        input_customernumber.isEnabled = !isLoading
        button_auth_login.isEnabled = !isLoading
        if (isLoading) {
            button_auth_login.visibility = View.INVISIBLE
            progressbar_auth.visibility = View.VISIBLE
        } else {
            button_auth_login.visibility = View.VISIBLE
            progressbar_auth.visibility = View.INVISIBLE
        }
    }

    private fun displaySnackbar(text: String) {
        Snackbar.make(constraintlayout_auth, text, Snackbar.LENGTH_LONG)
            .setAction("Dismiss") {}
            .show()
    }

    inner class EncryptCallback : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)

            val cipher: Cipher? = result.cryptoObject?.cipher
            if (cipher != null) {
                val data: LoginData? = viewModel.login.value?.data
                if (data != null) {
                    // encrypt the password
                    val password = data.password
                    val encryptedBytes: ByteArray =
                        cipher.doFinal(password.toByteArray(Charset.defaultCharset()))

                    // save encrypted password & init vector in sharedPreferences
                    val encryptedPassword = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
                    val iv = Base64.encodeToString(cipher.iv, Base64.DEFAULT)
                    sp.edit()
                        .putString(Constants.ENCRYPTED_PWD, encryptedPassword)
                        .putString(Constants.INITIALIZATION_VECTOR, iv)
                        .apply()

                    finishLogin(data, true)
                } else {
                    throw RuntimeException("Login credentials are null")
                }
            } else {
                throw RuntimeException("CryptoObject or Cipher is null")
            }
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            when (errorCode) {
                BiometricConstants.ERROR_NEGATIVE_BUTTON -> {
                    sp.edit()
                        .putBoolean(Constants.DONT_REMIND_BIOMETRIC, true)
                        .apply()

                    // finish login normally
                    val data: LoginData? = viewModel.login.value?.data
                    if (data != null) {
                        finishLogin(data)
                    } else {
                        throw RuntimeException("Login credentials are null")
                    }
                }
                else -> {
                    Toast.makeText(this@AuthActivity, errString, Toast.LENGTH_LONG).show()
                    setLoading(false)
                }
            }
        }
    }

    inner class DecryptCallback : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)

            setLoading(true)

            val cipher: Cipher? = result.cryptoObject?.cipher
            if (cipher != null) {
                val encryptedPassword: String? = sp.getString(Constants.ENCRYPTED_PWD, null)
                if (encryptedPassword != null) {
                    // decrypt the password
                    val encryptedBytes: ByteArray = Base64.decode(encryptedPassword, Base64.DEFAULT)
                    val decryptedBytes: ByteArray = cipher.doFinal(encryptedBytes)
                    val password = String(decryptedBytes, Charset.defaultCharset())

                    // do login
                    val username: String? = sp.getString(Constants.USERNAME, null)
                    val customerNum = sp.getInt(Constants.USER_ID, -1)
                    if (username != null && customerNum != -1) {
                        viewModel.login(username, password, customerNum)
                    } else {
                        throw RuntimeException("Username and customerNum are unexpectedly null")
                    }
                } else {
                    throw RuntimeException("Encrypted password was not found")
                }
            } else {
                throw RuntimeException("CryptoObject or Cipher is null")
            }
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            if (errorCode == BiometricConstants.ERROR_NEGATIVE_BUTTON) {
                decryptPrompt.cancelAuthentication()
            } else if (errorCode != BiometricConstants.ERROR_USER_CANCELED) {
                Toast.makeText(this@AuthActivity, errString, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private val TAG = AuthActivity::class.java.simpleName
    }
}
