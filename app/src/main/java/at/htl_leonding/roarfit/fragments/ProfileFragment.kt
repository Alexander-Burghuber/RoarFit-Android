package at.htl_leonding.roarfit.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.activities.AuthActivity
import at.htl_leonding.roarfit.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var model: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        // Observe the status from model.getUser()
        model.userStatus.observe(this, Observer { result ->
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                profile_customer_number.text = user.id.toString()
                profile_first_name.text = user.firstName
                profile_last_name.text = user.lastName

                profile_progress_bar.visibility = View.GONE
                profile_image_view.visibility = View.VISIBLE
            } else {
                startAuthActivity(result.exceptionOrNull()!!.message!!)
            }
        })

        // Inflate the layout for this fragment
        return view
    }

    private fun startAuthActivity(msg: String? = null) {
        val activity = requireActivity()
        val intent = Intent(activity, AuthActivity::class.java)

        if (msg != null) {
            intent.putExtra("msg", msg)
        }

        startActivity(intent)
        activity.finish()
    }

}
