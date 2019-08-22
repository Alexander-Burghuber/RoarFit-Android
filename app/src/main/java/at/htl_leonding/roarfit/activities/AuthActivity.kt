package at.htl_leonding.roarfit.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.utils.Constants
import at.htl_leonding.roarfit.viewmodels.AuthViewModel
import co.infinum.goldfinger.Goldfinger
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private lateinit var model: AuthViewModel
    private lateinit var goldfinger: Goldfinger

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        model = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        goldfinger = Goldfinger.Builder(this).setLogEnabled(true).build()

        model.loginResStatus.observe(this, Observer { result ->
            if (result.isSuccess) {
                val username = model.username
                val password = model.password
                val customerNum = model.customerNum

                if (username != null && password != null && customerNum != null) {
                    val jwt = result.getOrNull()!!.token
                    if (input_checkbox.isChecked) {
                        if (goldfinger.hasEnrolledFingerprint()) {

                            val dialog = MaterialAlertDialogBuilder(this)
                                .setTitle("Confirm Fingerprint")
                                .setMessage("Please put your finger on the scanner...\n")
                                .setCancelable(true)
                                .setOnCancelListener {
                                    setEnabledStateOfInput(true)
                                    setLoading(false)
                                }
                                .show()

                            goldfinger.encrypt(
                                "password",
                                password,
                                object : Goldfinger.Callback {
                                    override fun onResult(result: Goldfinger.Result) {
                                        when (result.type()) {
                                            Goldfinger.Type.SUCCESS -> {
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
                                                handleFingerprintDisabled(msg)
                                                dialog.cancel()
                                            }
                                        }
                                    }

                                    override fun onError(e: Exception) {
                                        handleFingerprintDisabled(
                                            "An unknown error occurred during fingerprint authentication setup.",
                                            e
                                        )
                                        dialog.cancel()
                                    }
                                })
                        } else {
                            displayToast("No fingerprint has been set on this device.\nPlease add one in the device settings.")
                            setEnabledStateOfInput(true)
                            setLoading(false)
                        }
                    } else {
                        finishLogin(username, jwt, customerNum)
                    }
                } else {
                    displayToast("An unexpected error occurred while logging in. Please try again.")
                }
            } else {
                displayToast(result.exceptionOrNull()!!.message!!)
                setEnabledStateOfInput(true)
                setLoading(false)
            }
        })

        if (goldfinger.hasFingerprintHardware()) input_checkbox.isEnabled = true

        button_login.setOnClickListener {
            setEnabledStateOfInput(false)
            hideKeyboard()

            val username = input_username.text.toString()
            val password = input_password.text.toString()
            val customerNumber = input_customer_number.text.toString()

            if (!username.isBlank() && !password.isBlank() && !customerNumber.isBlank()) {
                setLoading(true)
                model.login(username, password, customerNumber.toInt())
            } else {
                displayToast("Please fill in all fields")
                setEnabledStateOfInput(true)
            }
        }

        val sp = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)
        input_username.setText(sp.getString("username", ""))

        val username = sp.getString("username", null)
        val encryptedPwd = sp.getString("encrypted_pwd", null)
        val customerNum = sp.getInt("customer_num", -1)
        if (username != null && encryptedPwd != null && customerNum != -1) {
            loginWithFingerprint(username, encryptedPwd, customerNum)
        }
    }

    private fun finishLogin(username: String, jwt: String, customerNum: Int, encryptedPwd: String? = null) {
        val spEditor = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE).edit()
        spEditor.putString("username", username)
        spEditor.putString("jwt", jwt)
        spEditor.putInt("customer_num", customerNum)
        if (encryptedPwd != null) spEditor.putString("encrypted_pwd", encryptedPwd)
        spEditor.apply()

        setEnabledStateOfInput(true)
        setLoading(false)
        startMainActivity()
    }

    private fun loginWithFingerprint(username: String, encryptedPwd: String, customerNum: Int) {
        fingerprint_icon.visibility = View.VISIBLE
        goldfinger.decrypt("password", encryptedPwd, object : Goldfinger.Callback {
            override fun onResult(result: Goldfinger.Result) {
                when (result.type()) {
                    Goldfinger.Type.SUCCESS -> {
                        setEnabledStateOfInput(false)
                        setLoading(true)
                        val password = result.value()!!
                        model.login(username, password, customerNum)
                    }
                    Goldfinger.Type.INFO -> {
                        if (result.reason() == Goldfinger.Reason.AUTHENTICATION_FAIL) {
                            ImageViewCompat.setImageTintList(
                                fingerprint_icon,
                                ColorStateList.valueOf(ContextCompat.getColor(this@AuthActivity, R.color.error))
                            )
                            Handler().postDelayed(
                                {
                                    ImageViewCompat.setImageTintList(
                                        fingerprint_icon,
                                        ColorStateList.valueOf(ContextCompat.getColor(this@AuthActivity, R.color.grey))
                                    )
                                }, 250
                            )
                        }
                    }
                    Goldfinger.Type.ERROR -> {
                        val msg = if (result.reason() == Goldfinger.Reason.LOCKOUT) {
                            "Too many attempts, please login manually."
                        } else {
                            "An unknown error occurred during fingerprint scanning. Please login manually."
                        }
                        handleFingerprintDisabled(msg)
                    }
                }
            }

            override fun onError(e: Exception) {
                handleFingerprintDisabled(
                    "An unknown error occurred during fingerprint scanning. Please login manually.",
                    e
                )
            }
        })
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun displayToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun setEnabledStateOfInput(bool: Boolean) {
        input_username.isEnabled = bool
        input_password.isEnabled = bool
        input_customer_number.isEnabled = bool
        button_login.isEnabled = bool
        if (goldfinger.hasFingerprintHardware()) input_checkbox.isEnabled = bool
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            button_login.visibility = View.INVISIBLE
            progress_bar_auth.visibility = View.VISIBLE
        } else {
            button_login.visibility = View.VISIBLE
            progress_bar_auth.visibility = View.INVISIBLE
        }
    }

    private fun handleFingerprintDisabled(msg: String, e: Exception? = null) {
        goldfinger.cancel()
        input_checkbox.isChecked = false
        input_checkbox.visibility = View.INVISIBLE
        fingerprint_icon.visibility = View.INVISIBLE
        displayToast(msg)
        if (e != null) Log.e("AuthActivity", msg, e) else Log.d("AuthActivity", msg)
    }

}
