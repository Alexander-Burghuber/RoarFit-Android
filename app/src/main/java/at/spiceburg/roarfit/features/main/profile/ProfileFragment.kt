package at.spiceburg.roarfit.features.main.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()

        val activity = (requireActivity() as MainActivity)
        val sp = activity.getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)
        val jwt = sp.getString("jwt", null)
        val customerNum = sp.getInt("customer_num", -1)

        if (jwt != null && customerNum != -1) {
            val appContainer = (activity.application as MyApplication).appContainer
            val factory = ProfileViewModel.Factory(customerNum, appContainer.userRepository)
            viewModel = ViewModelProviders.of(this, factory).get(ProfileViewModel::class.java)

            // query the cached user from the database
            viewModel.user.observe(this) { user ->
                if (user != null) {
                    text_profile_customernum.text = user.id.toString()
                    text_profile_firstname.text = user.firstName
                    text_profile_lastname.text = user.lastName
                }
            }

            // load the user from the network
            viewModel.loadUser(jwt).observe(this) { status ->
                when (status) {
                    is Status.Success -> {
                        progress_profile.visibility = View.GONE
                    }
                    is Status.Loading -> {
                        progress_profile.visibility = View.VISIBLE
                    }
                    is Status.Error -> {
                        progress_profile.visibility = View.GONE
                        status.message?.let { displaySnackbar(it) }
                        if (status.logout) {
                            activity.logout()
                        }
                    }
                }
            }
        } else {
            displaySnackbar("Please re-login")
            activity.logout()
        }
    }

    private fun displaySnackbar(text: String) {
        Snackbar.make(constraintlayout_profile, text, Snackbar.LENGTH_SHORT)
            .setAction("Dismiss") {} // empty callback dismisses the snackbar by default
            .show()
    }
}
