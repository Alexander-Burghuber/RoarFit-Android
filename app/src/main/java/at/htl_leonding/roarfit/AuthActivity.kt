package at.htl_leonding.roarfit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        model.loginResult.observe(this, Observer { response ->
            if (response != null) {
                when (response.code) {
                    0 -> {
                        val sharedPre = getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                        sharedPre.edit().putString("auth_token", response.token).apply()
                        startMainActivity()
                    }
                    2 -> Toast.makeText(this, "Username or password is wrong", Toast.LENGTH_LONG).show()
                    else -> Log.e("AuthActivity", "Received unknown code")
                }
            } else {
                Toast.makeText(this, "Failed trying to log in", Toast.LENGTH_LONG).show()
            }
        })

        button_login.setOnClickListener {
            val username = input_username.text.toString()
            val password = input_password.text.toString()
            model.login(username, password)
            hideKeyboard()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

}
