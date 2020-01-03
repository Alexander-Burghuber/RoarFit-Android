package at.spiceburg.roarfit.features.main.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.WorkoutPlan

class WorkoutPlansAdapter(context: Context) :
    RecyclerView.Adapter<WorkoutPlansAdapter.WorkoutPlanViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var workoutPlans: Array<WorkoutPlan> = emptyArray()

    inner class WorkoutPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_workoutplan_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutPlanViewHolder {
        val itemView = inflater.inflate(R.layout.item_workoutplan, parent, false)
        return WorkoutPlanViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return workoutPlans.size
    }

    override fun onBindViewHolder(holder: WorkoutPlanViewHolder, position: Int) {
        val workoutPlan = workoutPlans[position]
        holder.name.text = workoutPlan.name
    }

    fun setWorkoutPlans(workoutPlans: Array<WorkoutPlan>) {
        this.workoutPlans = workoutPlans
        notifyDataSetChanged()
    }
}
