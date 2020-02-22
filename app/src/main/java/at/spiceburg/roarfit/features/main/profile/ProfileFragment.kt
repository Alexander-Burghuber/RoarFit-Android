package at.spiceburg.roarfit.features.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

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
        viewModel = activity.viewModel

        // query the user from the database
        viewModel.user.observe(this) { user ->
            text_profile_customernum.text = user.id.toString()
            text_profile_firstname.text = user.firstName
            text_profile_lastname.text = user.lastName
        }
    }

    private fun displaySnackbar(text: String) {
        Snackbar.make(constraintlayout_profile, text, Snackbar.LENGTH_SHORT)
            .setAction("Dismiss") {} // empty callback dismisses the snackbar by default
            .show()
    }
}
