package at.spiceburg.roarfit.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.ui.auth.AuthActivity
import at.spiceburg.roarfit.ui.workout.WorkoutActivity
import at.spiceburg.roarfit.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        // setup navigation
        val navController = findNavController(this, R.id.navhostfragment_main)
        bottomnav_main.setupWithNavController(navController)

        // setup configuration with top-level destinations
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.dashboardFragment,
            R.id.statisticsFragment,
            R.id.historyFragment,
            R.id.profileFragment
        ).build()
        setupActionBarWithNavController(navController, appBarConfiguration)

        // fab button click listener
        fab_main.setOnClickListener {
            // check if the permission to use the camera has been granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // permission has not been granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    Constants.PERMISSION_REQUEST_CODE_CAMERA
                )
            } else {
                // permission has already been granted
                startWorkoutActivity()
            }
        }

        val sp = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)

        // reset the database if the build is for debug and not for production
        /*if (BuildConfig.DEBUG) {
            this.deleteDatabase("roarfit_database")
            sp.edit().putInt("db_initialised_version", 0).apply()
        }*/

        // check if the db has been initialised on this app version before
        val appVersion = packageManager.getPackageInfo(packageName, 0).versionCode
        if (sp.getInt("db_initialised_version", 0) < appVersion) {
            // if not, then create the db with the needed content before continuing the data loading
            viewModel.initDatabase().observe(this, Observer {
                Log.d(TAG, "Initialised database")
                sp.edit().putInt("db_initialised_version", appVersion).apply()
            })
        }

        viewModel.getAllExerciseTemplates().observe(this, Observer { exerciseTemplates ->
            var output = ""
            exerciseTemplates.forEach {
                output += "\nname: ${it.name} equipment: ${it.equipment} bodyPart: ${it.bodyPart}"
            }
            Log.d(TAG, "Found ${exerciseTemplates.size} exercise templates: $output")
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_REQUEST_CODE_CAMERA) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission granted
                startWorkoutActivity()
            } else {
                // permission denied
                Snackbar.make(
                    findViewById(R.id.constraintlayout_main),
                    "Please allow the needed permissions to use this feature",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_app_bar_settings -> true
            R.id.menu_app_bar_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun logout() {
        val spEditor = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE).edit()
        spEditor.remove("username")
            .remove("jwt")
            .remove("customer_num")
            .remove("encrypted_pwd")
            .apply()
        startAuthActivity()
    }

    fun displayToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun startAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startWorkoutActivity() {
        val intent = Intent(this, WorkoutActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
