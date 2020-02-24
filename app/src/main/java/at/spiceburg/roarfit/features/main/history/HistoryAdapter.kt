package at.spiceburg.roarfit.features.main.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.Exercise
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val context: Context) :
    RecyclerView.Adapter<HistoryAdapter.ExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exercises: MutableList<Exercise> = mutableListOf()
    private val timeFormatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
    private val dateFormatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_itemhistory_name)
        val info: TextView = itemView.findViewById(R.id.text_itemhistory_info)
        val time: TextView = itemView.findViewById(R.id.text_itemhistory_time)
        val date: TextView = itemView.findViewById(R.id.text_itemhistory_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = inflater.inflate(R.layout.item_history, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise: Exercise = exercises[position]
        holder.name.text = exercise.template.name


        var infoString = context.getString(
            R.string.history_exercise_info,
            exercise.time,
            exercise.sets,
            exercise.reps
        )

        val weight: String? = exercise.weight
        if (!weight.isNullOrBlank()) {
            infoString += context.getString(R.string.history_exercise_opt_weight, weight)
        }
        holder.info.text = infoString

        exercise.completedDate?.let {
            val date = Date(it)
            holder.time.text = timeFormatter.format(date)
            holder.date.text = dateFormatter.format(date)
        }
    }

    fun setExercises(exercises: Array<Exercise>) {
        this.exercises = exercises.toMutableList()
        notifyDataSetChanged()
    }

    fun addMoreExercise(exercises: Array<Exercise>, view: RecyclerView?) {
        val oldItemCount = itemCount
        this.exercises.addAll(exercises)
        notifyDataSetChanged()
        view?.post {
            notifyItemRangeInserted(oldItemCount, this.exercises.size - 1)
        }
    }
}
