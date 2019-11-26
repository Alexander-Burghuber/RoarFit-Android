package at.spiceburg.roarfit.features.main.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.utils.Constants
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(
            this,
            ProfileViewModel.Factory(requireContext())
        ).get(ProfileViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        val mainActivity = (requireActivity() as MainActivity)
        val sp = mainActivity.getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)

        val jwt = sp.getString("jwt", null)
        val customerNum = sp.getInt("customer_num", -1)
        if (jwt != null && customerNum != -1) {
            viewModel.getUser(customerNum, jwt).observe(this, Observer { res ->
                when (res) {
                    is Resource.Success -> {
                        displayProfile(res.data!!)
                        progressbar_profile.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        progressbar_profile.visibility = View.VISIBLE
                        constraintlayout_profile.visibility = View.INVISIBLE

                        res.data?.let { user -> displayProfile(user) }
                    }
                    is Resource.Error -> {
                        val msg = res.message!!
                        if (res.logout!!) {
                            mainActivity.displayToast("$msg Please re-login.")
                            mainActivity.logout()
                        } else {
                            mainActivity.displayToast(msg)
                            progressbar_profile.visibility = View.GONE
                        }
                    }
                }
            })
        } else {
            mainActivity.logout()
        }
    }

    private fun displayProfile(user: User) {
        text_profile_customernumber.text = user.id.toString()
        text_profile_firstname.text = user.firstName
        text_profile_lastname.text = user.lastName

        constraintlayout_profile.visibility = View.VISIBLE
    }
}
