package at.htlleonding.roarfit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.htlleonding.roarfit.R
import at.htlleonding.roarfit.data.Resource
import at.htlleonding.roarfit.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        sharedViewModel.userLD.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    val user = resource.data!!
                    profile_customer_number.text = user.id.toString()
                    profile_first_name.text = user.firstName
                    profile_last_name.text = user.lastName

                    profile_progress_bar.visibility = View.GONE
                    profile_layout.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    profile_progress_bar.visibility = View.VISIBLE
                    profile_layout.visibility = View.INVISIBLE
                }
            }
        })
    }
}