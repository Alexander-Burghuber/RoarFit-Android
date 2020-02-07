package at.spiceburg.roarfit.data.entities

data class WorkoutPlan(
    val id: Int,
    val name: String,
    val warmup: String,
    val cooldown: String,
    val workouts: List<Workout>
)
