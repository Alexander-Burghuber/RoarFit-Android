package at.spiceburg.roarfit.features.main.equipmentlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Equipment
import kotlinx.android.synthetic.main.fragment_equipment_list.*

class EquipmentListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equipment_list, container, false)
    }

    override fun onStart() {
        super.onStart()

        val onEquipmentClicked: (exerciseTemplate: Equipment) -> Unit = { equipment ->
            val action = EquipmentListFragmentDirections
                .actionEquipmentListFragmentToExerciseListFragment(equipment)
            findNavController().navigate(action)
        }

        val adapter = EquipmentListAdapter(requireContext(), onEquipmentClicked)
        recyclerview_equipmentlist_equipments.adapter = adapter
        recyclerview_equipmentlist_equipments.layoutManager = LinearLayoutManager(requireContext())
        adapter.setExerciseTemplates(Equipment.values().toList())
    }
}
