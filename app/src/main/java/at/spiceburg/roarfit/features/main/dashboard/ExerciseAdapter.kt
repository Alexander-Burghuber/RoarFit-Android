package at.spiceburg.roarfit.features.main.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.UserExercise

class ExerciseAdapter(context: Context) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exercises: List<UserExercise> = emptyList()

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_itemexerciseinfo_exercisename)
        val bodyPart: TextView = itemView.findViewById(R.id.text_itemexerciseinfo_bodypart)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExerciseViewHolder {
        val itemView = inflater.inflate(R.layout.item_exerciseinfo, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.name.text = exercise.template.name
        holder.bodyPart.text = exercise.template.bodyPart
    }

    fun setExercises(exercises: List<UserExercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }
}