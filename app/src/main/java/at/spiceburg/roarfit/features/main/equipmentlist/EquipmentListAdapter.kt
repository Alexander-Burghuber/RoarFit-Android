package at.spiceburg.roarfit.features.main.equipmentlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.Equipment

class EquipmentListAdapter(context: Context, val onEquipmentClicked: (Equipment) -> Unit) :
    RecyclerView.Adapter<EquipmentListAdapter.EquipmentViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var equipment: List<Equipment> = emptyList()

    inner class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val equipmentName: TextView = itemView.findViewById(R.id.text_itemequipment_name)

        init {
            itemView.setOnClickListener {
                onEquipmentClicked.invoke(equipment[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val itemView = inflater.inflate(R.layout.item_equipment, parent, false)
        return EquipmentViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return equipment.size
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val equipment = equipment[position]
        holder.equipmentName.text = equipment.string
    }

    fun setExerciseTemplates(equipment: List<Equipment>) {
        this.equipment = equipment
        notifyDataSetChanged()
    }
}