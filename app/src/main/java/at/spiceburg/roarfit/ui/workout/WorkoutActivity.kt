package at.spiceburg.roarfit.ui.workout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import at.spiceburg.roarfit.R
import kotlinx.android.synthetic.main.activity_workout.*

class WorkoutActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // navController = findNavController(R.id.navhostfragment_workout)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setSupportActionBar(toolbar_workout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        /*  navController.addOnDestinationChangedListener { _, destination, _ ->
              if (destination.id == R.id.equipmentListFragment) {
                  val closeIcon = getDrawable(R.drawable.ic_close_black_24dp)!!
                  closeIcon.setTint(resources.getColor(R.color.white, null))
                  supportActionBar?.setHomeAsUpIndicator(closeIcon)
              }
          }*/

        /*onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })*/
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /*fun setupExerciseListLayout() {
        val closeIcon = getDrawable(R.drawable.ic_close_black_24dp)!!
        closeIcon.setTint(resources.getColor(R.color.white, null))
        supportActionBar!!.setHomeAsUpIndicator(closeIcon)

        *//*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.navigationBarColor = resources.getColor(R.color.lightGrey, null)*//*
    }

    fun setupExerciseInfoLayout() {
        val backIcon = getDrawable(R.drawable.ic_arrow_back_black_24dp)!!
        backIcon.setTint(resources.getColor(R.color.white, null))
        supportActionBar!!.setHomeAsUpIndicator(backIcon)
    }*/
}
