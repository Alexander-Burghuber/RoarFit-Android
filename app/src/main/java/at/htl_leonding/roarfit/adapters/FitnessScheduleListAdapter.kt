package at.htl_leonding.roarfit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.model.FitnessSchedule

class FitnessScheduleListAdapter(context: Context) :
    RecyclerView.Adapter<FitnessScheduleListAdapter.FitnessScheduleViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var fitnessSchedules: List<FitnessSchedule> = emptyList()

    inner class FitnessScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.fitness_schedule_list_item_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FitnessScheduleViewHolder {
        val itemView = inflater.inflate(R.layout.fitness_schedule_list_item, parent, false)
        return FitnessScheduleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return fitnessSchedules.size
    }

    override fun onBindViewHolder(holder: FitnessScheduleViewHolder, position: Int) {
        val fitnessSchedule = fitnessSchedules[position]
        holder.name.text = fitnessSchedule.name
    }

    fun setFitnessSchedules(fitnessSchedules: List<FitnessSchedule>) {
        this.fitnessSchedules = fitnessSchedules
        notifyDataSetChanged()
    }
}