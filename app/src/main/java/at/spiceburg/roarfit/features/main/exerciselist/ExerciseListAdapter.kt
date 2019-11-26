package at.spiceburg.roarfit.features.main.exerciselist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseTemplate

class ExerciseListAdapter(context: Context, val onExerciseClicked: (ExerciseTemplate) -> Unit) :
    RecyclerView.Adapter<ExerciseListAdapter.ExerciseViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var exerciseTemplates: List<ExerciseTemplate> = emptyList()

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.text_itemexerciseinfo_exercisename)
        val bodyPart: TextView = itemView.findViewById(R.id.text_itemexerciseinfo_bodypart)

        init {
            itemView.setOnClickListener {
                onExerciseClicked.invoke(exerciseTemplates[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = inflater.inflate(R.layout.item_exerciseinfo, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exerciseTemplates.size
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exerciseTemplate = exerciseTemplates[position]
        holder.exerciseName.text = exerciseTemplate.name
        holder.bodyPart.text = exerciseTemplate.bodyPart
    }

    fun setExerciseTemplates(exerciseTemplates: List<ExerciseTemplate>) {
        this.exerciseTemplates = exerciseTemplates
        notifyDataSetChanged()
    }
}