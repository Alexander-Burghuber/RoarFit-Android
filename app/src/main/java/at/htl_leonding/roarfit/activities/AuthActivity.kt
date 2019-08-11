package at.htl_leonding.roarfit.activities

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.utils.Constants
import at.htl_leonding.roarfit.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private lateinit var model: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        model = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        model.loginResStatus.observe(this, Observer { result ->
            if (result.isSuccess) {
                val am = AccountManager.get(this)
                val account = Account(input_username.text.toString(), Constants.ACCOUNT_TYPE)

                val userdata = Bundle()
                userdata.putInt("customerNum", input_customer_number.text.toString().toInt())

                am.addAccountExplicitly(account, input_password.text.toString(), userdata)
                am.setAuthToken(account, "full_access", result.getOrNull()!!.token)

                startMainActivity()
            } else {
                displayToast(result.exceptionOrNull()!!.message!!)
            }
            setEnabledStateOfInput(true)
            progress_bar_auth.visibility = View.INVISIBLE
        })

        button_login.setOnClickListener {
            setEnabledStateOfInput(false)
            hideKeyboard()

            val username = input_username.text.toString()
            val password = input_password.text.toString()
            val customerNumber = input_customer_number.text.toString()

            if (username.isNullOrBlank() || password.isNullOrBlank() || customerNumber.isNullOrBlank()) {
                displayToast("Please fill in all fields")
                setEnabledStateOfInput(true)
            } else {
                progress_bar_auth.visibility = View.VISIBLE
                model.login(username, password)
            }
        }

        // display an error msg if one was received from another activity
        val msg = intent.getStringExtra("msg")
        if (msg != null) {
            displayToast(msg)
        }
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
    }

}
