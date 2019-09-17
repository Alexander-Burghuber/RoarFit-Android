package at.spiceburg.roarfit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.viewmodels.SharedViewModel
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
                    resource.data?.let { user ->
                        text_profile_customernumber.text = user.id.toString()
                        text_profile_firstname.text = user.firstName
                        text_profile_lastname.text = user.lastName

                        progressbar_profile.visibility = View.GONE
                        constraintlayout_profile.visibility = View.VISIBLE
                    }
                }
                is Resource.Loading -> {
                    progressbar_profile.visibility = View.VISIBLE
                    constraintlayout_profile.visibility = View.INVISIBLE
                }
            }
        })
    }
}