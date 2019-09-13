package at.htl_leonding.roarfit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.data.entities.WorkoutPlan

class WorkoutPlanListAdapter(context: Context) :
    RecyclerView.Adapter<WorkoutPlanListAdapter.WorkoutPlanViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var workoutPlans: List<WorkoutPlan> = emptyList()

    inner class WorkoutPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.workout_plan_list_item_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutPlanViewHolder {
        val itemView = inflater.inflate(R.layout.workout_plan_list_item, parent, false)
        return WorkoutPlanViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return workoutPlans.size
    }

    override fun onBindViewHolder(holder: WorkoutPlanViewHolder, position: Int) {
        val workoutPlan = workoutPlans[position]
        holder.name.text = workoutPlan.name
    }

    fun setWorkoutPlans(workoutPlans: List<WorkoutPlan>) {
        this.workoutPlans = workoutPlans
        notifyDataSetChanged()
    }
}