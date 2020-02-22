package at.spiceburg.roarfit.features.main.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseSpecification
import at.spiceburg.roarfit.data.entities.Workout

class WorkoutsAdapter(
    private val context: Context, private val onExerciseClicked: (ExerciseSpecification) -> Unit
) : RecyclerView.Adapter<WorkoutsAdapter.WorkoutPlanViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val workouts: SortedList<Workout> =
        SortedList(Workout::class.java, object : SortedList.Callback<Workout>() {
            override fun areItemsTheSame(w1: Workout, w2: Workout): Boolean {
                return false
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
            }

            override fun onChanged(position: Int, count: Int) {
            }

            override fun onInserted(position: Int, count: Int) {
            }

            override fun onRemoved(position: Int, count: Int) {
            }

            override fun compare(w1: Workout, w2: Workout): Int {
                if (w1.day > w2.day) {
                    return 1
                } else if (w1.day < w2.day) {
                    return -1
                }
                return 0
            }

            override fun areContentsTheSame(oldItem: Workout?, newItem: Workout?): Boolean {
                return false
            }
        })

    inner class WorkoutPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day: TextView = itemView.findViewById(R.id.text_workoutplan_day)
        val exerciseList: RecyclerView = itemView.findViewById(R.id.list_workout_exercises)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutPlanViewHolder {
        val itemView = inflater.inflate(R.layout.item_workout, parent, false)
        return WorkoutPlanViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return workouts.size()
    }

    override fun onBindViewHolder(holder: WorkoutPlanViewHolder, position: Int) {
        val workout = workouts[position]
        holder.day.text =
            context.resources.getString(R.string.dashboard_workoutplan_day, workout.day)
        val adapter = SpecificationsAdapter(context, onExerciseClicked)
        holder.exerciseList.adapter = adapter
        holder.exerciseList.layoutManager = LinearLayoutManager(context)
        adapter.setSpecifications(workout.specifications)
    }

    fun addWorkouts(workouts: List<Workout>) {
        this.workouts.addAll(workouts)
        notifyDataSetChanged()
    }
}
