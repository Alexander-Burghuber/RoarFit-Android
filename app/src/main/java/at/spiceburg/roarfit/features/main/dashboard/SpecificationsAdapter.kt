package at.spiceburg.roarfit.features.main.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.data.entities.ExerciseSpecification

class SpecificationsAdapter(
    context: Context, private val onExerciseClicked: (ExerciseSpecification) -> Unit
) : RecyclerView.Adapter<SpecificationsAdapter.ExerciseViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var specifications: List<ExerciseSpecification> = emptyList()

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_itemexerciseinfo_exercisename)
        val bodyPart: TextView = itemView.findViewById(R.id.text_itemexerciseinfo_bodypart)

        init {
            itemView.setOnClickListener {
                onExerciseClicked.invoke(specifications[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExerciseViewHolder {
        val itemView = inflater.inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return specifications.size
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val specification = specifications[position]
        holder.name.text = specification.exercise.template.name

        val bodyParts: List<String> = specification.exercise.template.bodyParts
        var bodyPartsStr = ""
        for (i in bodyParts.indices) {
            bodyPartsStr += if (i == 0) bodyParts[i] else ", ${bodyParts[i]}"
        }
        holder.bodyPart.text = bodyPartsStr
    }

    fun setSpecifications(specifications: List<ExerciseSpecification>) {
        this.specifications = specifications
        notifyDataSetChanged()
    }
}