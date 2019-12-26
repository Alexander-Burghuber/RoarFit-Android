package at.spiceburg.roarfit.features.auth

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.utils.Constants
import co.infinum.goldfinger.Goldfinger
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var goldfinger: Goldfinger

    override fun onCreate(savedInstanceState: Bundle?) {
        // replaces the launcher theme with the normal one
        setTheme(R.style.AppTheme_Auth)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val appContainer = (application as MyApplication).appContainer
        viewModel = ViewModelProviders.of(this, AuthViewModel.Factory(appContainer.keyFitApi))
            .get(AuthViewModel::class.java)

        goldfinger = Goldfinger.Builder(this).setLogEnabled(true).build()

        // observe the status of the login network request
        viewModel.loginLD.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    val data = resource.data!!
                    val sp = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)
                    if (!sp.getBoolean(
                            "dont_remind_fingerprint",
                            false
                        ) && !sp.contains("encrypted_pwd") && goldfinger.hasFingerprintHardware()
                    ) {
                        setupFingerprintAuth(
                            data.username,
                            data.password,
                            data.customerNum,
                            data.token
                        )
                    } else {
                        finishLogin(data.username, data.token, data.customerNum)
                    }
                    setLoading(false)
                }
                is Resource.Loading -> {
                    setLoading(true)
                }
                is Resource.Error -> {
                    displaySnackbar(resource.message!!)
                    setEnabledStateOfInput(true)
                    setLoading(false)
                }
            }
        })

        button_auth_login.setOnClickListener {
            setEnabledStateOfInput(false)
            hideKeyboard()

            val username = input_username.text.toString()
            val password = input_password.text.toString()
            val customerNumber = input_customernumber.text.toString()

            if (!username.isBlank() && !password.isBlank() && !customerNumber.isBlank()) {
                viewModel.login(username, password, customerNumber.toInt())
            } else {
                displaySnackbar("Please fill in all fields.")
                setEnabledStateOfInput(true)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sp = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)

        val username = sp.getString("username", null)
        input_username.setText(username ?: "")
        val encryptedPwd = sp.getString("encrypted_pwd", null)
        val customerNum = sp.getInt("customer_num", -1)

        // activate fingerprint authentication if the requirements are met
        if (username != null && encryptedPwd != null && customerNum != -1) {
            image_auth_fingerprint.visibility = View.VISIBLE
            text_auth_fingerprint.visibility = View.VISIBLE
            // decrypt the stored password for login using the fingerprint
            goldfinger.decrypt("password", encryptedPwd, DecryptCallback(username, customerNum))
        }
    }

    private fun setupFingerprintAuth(
        username: String,
        password: String,
        customerNum: Int,
        jwt: String
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Fingerprint Authentication")
            .setMessage("Activate fingerprint authentication to quickly login the next time you open the app.")
            .setCancelable(false)
            .setPositiveButton("Yes, please") { dialogInterface, _ ->
                if (goldfinger.hasEnrolledFingerprint()) {
                    // encrypt the password using the fingerprint
                    goldfinger.encrypt(
                        "password",
                        password,
                        EncryptCallback(username, jwt, customerNum)
                    )
                } else {
                    dialogInterface.cancel()
                    displaySnackbar(
                        "No fingerprint has been set on this device." +
                                " Please add one in the device settings."
                    )
                    setEnabledStateOfInput(true)
                }
            }
            .setNeutralButton("Don't remind me again") { dialogInterface, _ ->
                dialogInterface.dismiss()
                getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("dont_remind_fingerprint", true)
                    .apply()
                finishLogin(username, jwt, customerNum)
            }.show()
    }

    private fun finishLogin(
        username: String,
        jwt: String,
        customerNum: Int,
        encryptedPwd: String? = null
    ) {
        val spEditor = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE).edit()
        spEditor.putString("username", username)
        spEditor.putString("jwt", jwt)
        spEditor.putInt("customer_num", customerNum)
        // if the password has been encrypted (using the fingerprint), store it for the next login
        if (encryptedPwd != null) spEditor.putString("encrypted_pwd", encryptedPwd)
        spEditor.apply()

        setEnabledStateOfInput(true)
        startMainActivity()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun displaySnackbar(text: String) {
        Snackbar.make(constraintlayout_auth, text, Snackbar.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun setEnabledStateOfInput(bool: Boolean) {
        input_username.isEnabled = bool
        input_password.isEnabled = bool
        input_customernumber.isEnabled = bool
        button_auth_login.isEnabled = bool
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            button_auth_login.visibility = View.INVISIBLE
            progressbar_auth.visibility = View.VISIBLE
        } else {
            button_auth_login.visibility = View.VISIBLE
            progressbar_auth.visibility = View.INVISIBLE
        }
    }

    private fun handleDisabledFingerprint(msg: String?, e: Exception? = null) {
        goldfinger.cancel()
        image_auth_fingerprint.visibility = View.INVISIBLE
        text_auth_fingerprint.visibility = View.INVISIBLE
        if (msg != null) {
            displaySnackbar(msg)
            if (e != null) Log.e("AuthActivity", msg, e) else Log.d("AuthActivity", msg)
        }
    }

    private inner class EncryptCallback(
        private val username: String,
        private val jwt: String,
        private val customerNum: Int
    ) : Goldfinger.Callback {
        // display a dialog to inform the user
        private val dialog = MaterialAlertDialogBuilder(this@AuthActivity)
            .setTitle("Waiting for scan...")
            .setView(R.layout.dialog_fingerprint)
            .setOnCancelListener {
                setEnabledStateOfInput(true)
            }.show()

        override fun onResult(result: Goldfinger.Result) {
            when (result.type()) {
                Goldfinger.Type.SUCCESS -> {
                    // successfully encrypted the password
                    dialog.dismiss()
                    finishLogin(username, jwt, customerNum, result.value())
                }
                Goldfinger.Type.INFO -> {
                }
                Goldfinger.Type.ERROR -> {
                    val msg = if (result.reason() == Goldfinger.Reason.LOCKOUT) {
                        "Too many attempts, please login manually."
                    } else {
                        "An unknown error occurred during fingerprint scanning. Please login manually."
                    }
                    handleDisabledFingerprint(msg)
                    dialog.cancel()
                }
            }
        }

        override fun onError(e: Exception) {
            handleDisabledFingerprint(
                "An unknown error occurred during fingerprint authentication setup.", e
            )
            dialog.cancel()
        }
    }

    private inner class DecryptCallback(
        private val username: String,
        private val customerNum: Int
    ) : Goldfinger.Callback {
        override fun onResult(result: Goldfinger.Result) {
            when (result.type()) {
                Goldfinger.Type.SUCCESS -> {
                    // login with the decrypted password
                    val password = result.value()!!
                    viewModel.login(username, password, customerNum)
                }
                Goldfinger.Type.INFO -> {
                    if (result.reason() == Goldfinger.Reason.AUTHENTICATION_FAIL) {
                        // makes the fingerprint icon red for a small time to display the authentication failure
                        ImageViewCompat.setImageTintList(
                            image_auth_fingerprint,
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    this@AuthActivity,
                                    R.color.error
                                )
                            )
                        )
                        Handler().postDelayed({
                            ImageViewCompat.setImageTintList(
                                image_auth_fingerprint,
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        this@AuthActivity,
                                        R.color.grey
                                    )
                                )
                            )
                        }, 250)
                    }
                }
                Goldfinger.Type.ERROR -> {
                    val msg = when (result.reason()) {
                        Goldfinger.Reason.LOCKOUT -> "Too many attempts, please login manually."
                        Goldfinger.Reason.CANCELED -> null
                        else -> "An unknown error occurred during fingerprint scanning. Please login manually."
                    }
                    handleDisabledFingerprint(msg)
                }
            }
        }

        override fun onError(e: Exception) {
            handleDisabledFingerprint(
                "An unknown error occurred during fingerprint scanning. Please login manually.", e
            )
        }
    }
}
