package at.spiceburg.roarfit.features.main.equipmentlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.MainViewModel
import at.spiceburg.roarfit.utils.Constants
import kotlinx.android.synthetic.main.fragment_equipment_list.*

class EquipmentListFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equipment_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        val onEquipmentClicked: (equipment: String) -> Unit = { equipment ->
            val action =
                EquipmentListFragmentDirections.actionEquipmentListToExerciseList(equipment)
            findNavController().navigate(action)
        }

        val adapter = EquipmentListAdapter(requireContext(), onEquipmentClicked)
        recyclerview_equipmentlist_equipments.adapter = adapter
        recyclerview_equipmentlist_equipments.layoutManager = LinearLayoutManager(requireContext())

        val activity = (requireActivity() as MainActivity)
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString(Constants.JWT, null)!!

        viewModel.getEquipment(jwt).observe(this) { res ->
            when (res) {
                is Response.Success -> {
                    val equipment: Array<String> = res.data!!
                    adapter.setExerciseTemplates(equipment)
                }
                is Response.Loading -> {
                    // todo
                }
                is Response.Error -> {
                    // todo
                }
            }
        }
    }

    companion object {
        private val TAG = EquipmentListFragment::class.java.simpleName
    }
}
