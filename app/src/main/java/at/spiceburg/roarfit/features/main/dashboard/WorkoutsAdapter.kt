package at.spiceburg.roarfit.features.main.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.Workout

class WorkoutsAdapter(
    private val context: Context
) : RecyclerView.Adapter<WorkoutsAdapter.WorkoutPlanViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var workouts: List<Workout> = emptyList()

    inner class WorkoutPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day: TextView = itemView.findViewById(R.id.text_workoutplan_day)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutPlanViewHolder {
        val itemView = inflater.inflate(R.layout.item_workout, parent, false)
        return WorkoutPlanViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    override fun onBindViewHolder(holder: WorkoutPlanViewHolder, position: Int) {
        val workoutPlan = workouts[position]
        holder.day.text =
            context.resources.getString(R.string.dashboard_workoutplan_day, workoutPlan.day)
    }

    fun setWorkouts(workouts: List<Workout>) {
        this.workouts = workouts
        notifyDataSetChanged()
    }
}
