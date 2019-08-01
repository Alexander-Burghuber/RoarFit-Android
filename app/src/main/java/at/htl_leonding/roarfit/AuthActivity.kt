package at.htl_leonding.roarfit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
                val sharedPre = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                val editor = sharedPre.edit()

                val authToken = result.getOrNull()!!.token
                editor.putString("auth_token", authToken)

                val customerNumber = input_customer_number.text.toString().toInt()
                editor.putInt("customer_number", customerNumber)

                editor.apply()
                startMainActivity()
            } else {
                displayToast(result.exceptionOrNull()!!.message!!)
            }
            button_login.isEnabled = true
            progress_bar_auth.visibility = View.INVISIBLE
        })

        /*
        model.getCustomerResult.observe(this, Observer { result ->
            if (result.isSuccess) {
                startMainActivity()
            } else {
                displayToast(result.exceptionOrNull()!!.message!!)
            }
        })
        */

        button_login.setOnClickListener {
            hideKeyboard()
            val username = input_username.text.toString()
            val password = input_password.text.toString()
            val customerNumber = input_customer_number.text.toString()

            if (username.isNullOrBlank() || password.isNullOrBlank() || customerNumber.isNullOrBlank()) {
                displayToast("Please fill in all fields")
            } else {
                button_login.isEnabled = false
                progress_bar_auth.visibility = View.VISIBLE
                model.login(username, password)
            }
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

}
