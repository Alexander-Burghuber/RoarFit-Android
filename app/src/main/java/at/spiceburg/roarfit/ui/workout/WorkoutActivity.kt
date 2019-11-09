package at.spiceburg.roarfit.ui.workout

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import at.spiceburg.roarfit.R
import kotlinx.android.synthetic.main.fragment_exercise_info.*

class WorkoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finish()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    fun setupExerciseFragment() {
        setSupportActionBar(toolbar_exerciselist)
        val navController = findNavController(R.id.navhostfragment_workout)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val closeIcon = getDrawable(R.drawable.ic_close_black_24dp)
        closeIcon!!.setTint(resources.getColor(R.color.white, null))
        supportActionBar?.setHomeAsUpIndicator(closeIcon)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.navigationBarColor = resources.getColor(R.color.lightGrey, null)
    }
}
