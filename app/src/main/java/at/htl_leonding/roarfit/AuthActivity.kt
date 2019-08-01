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

        model.loginResult.observe(this, Observer { result ->
            if (result.isSuccess) {
                // TODO parse token after finished
                val sharedPre = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                val authToken = result.getOrNull()!!.token
                sharedPre.edit().putString("auth_token", authToken).apply()
                //startMainActivity()
                val customerNum = input_customer_number.text.toString()
                customerNum.removePrefix("KFC")
                model.getCustomer(customerNum, authToken)
            } else {
                displayToast(result.exceptionOrNull()!!.message!!)
                button_login.isEnabled = true
                progress_bar.visibility = View.INVISIBLE
            }
        })

        model.getCustomerResult.observe(this, Observer { result ->
            if (result.isSuccess) {
                startMainActivity()
            } else {
                displayToast(result.exceptionOrNull()!!.message!!)
            }
            button_login.isEnabled = true
            progress_bar.visibility = View.INVISIBLE
        })

        button_login.setOnClickListener {
            button_login.isEnabled = false
            progress_bar.visibility = View.VISIBLE

            val username = input_username.text.toString()
            val password = input_password.text.toString()

            model.login(username, password)
            hideKeyboard()
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
