package at.spiceburg.roarfit.features.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = (requireActivity() as MainActivity)

        viewModel.getUser().observe(viewLifecycleOwner) { res ->
            when {
                res.isSuccess() -> {
                    activity.progress_main?.hide()
                    val user: User = res.data!!
                    text_profile_customernum.text = user.id.toString()
                    text_profile_firstname.text = user.firstName
                    text_profile_lastname.text = user.lastName
                }
                res.isLoading() -> {
                    activity.progress_main?.show()
                }
                else -> {
                    activity.progress_main?.hide()
                    activity.handleNetworkError(res.error!!)
                }
            }
        }
    }
}
