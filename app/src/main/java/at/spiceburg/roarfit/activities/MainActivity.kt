package at.spiceburg.roarfit.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.utils.Constants
import at.spiceburg.roarfit.viewmodels.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

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
                startCameraActivity()
            }
        }

        val sp = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)
        val appVersion = packageManager.getPackageInfo(packageName, 0).versionCode

        // check if the db has been initialised on this app version before
        if (sp.getInt("db_initialised_version", 0) < appVersion) {
            // if not, then create the db with the needed content before continuing the data loading
            sharedViewModel.initDatabase().observe(this, Observer {
                sp.edit().putInt("db_initialised_version", appVersion).apply()
                loadData(sp)
            })
        } else {
            loadData(sp)
        }

        sharedViewModel.userLD.observe(this, Observer { resource ->
            Log.d(TAG, "observe resource: data: ${resource.data} message: ${resource.message}")
            when (resource) {
                is Resource.Error -> {
                    displayToast("${resource.message} Please re-login.")
                    logout()
                }
            }
        })

        sharedViewModel.exerciseHistoryLD.observe(this, Observer { exercises ->
            exercises.forEach { exercise ->
                Log.d("MainActivity", exercise.toString())
            }
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
                startCameraActivity()
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

    private fun logout() {
        val spEditor = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE).edit()
        spEditor.remove("username")
            .remove("jwt")
            .remove("customer_num")
            .remove("encrypted_pwd")
            .apply()
        startAuthActivity()
    }

    private fun loadData(sp: SharedPreferences) {
        val jwt = sp.getString("jwt", null)
        val customerNum = sp.getInt("customer_num", -1)
        if (jwt != null && customerNum != -1) {
            sharedViewModel.getUser(customerNum, jwt)
        } else {
            logout()
        }
        sharedViewModel.addUserExercise()
        sharedViewModel.loadExerciseHistory()
    }

    private fun startAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startCameraActivity() {
        val intent = Intent(this, WorkoutActivity::class.java)
        startActivity(intent)
    }

    private fun displayToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
