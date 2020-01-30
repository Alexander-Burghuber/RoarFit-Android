package at.spiceburg.roarfit.features.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.features.auth.AuthActivity
import at.spiceburg.roarfit.features.settings.SettingsActivity
import at.spiceburg.roarfit.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomSheetExerciseAction.ClickListener {

    lateinit var viewModel: MainViewModel
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)

        // check if user id and jwt is available
        val userId = sp.getInt(Constants.USER_ID, -1)
        val jwt: String? = sp.getString(Constants.JWT, null)
        if (userId == -1 || jwt == null) {
            logout(true)
        }

        // setup viewModel
        val appContainer = (application as MyApplication).appContainer
        val factory = MainViewModel.Factory(
            userId,
            appContainer.userRepository,
            appContainer.exerciseRepository,
            appContainer.workoutRepository
        )
        viewModel = ViewModelProviders.of(this, factory)
            .get(MainViewModel::class.java)

        // setup navigation
        setSupportActionBar(toolbar_main)
        navController = findNavController(this, R.id.navhost_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment, R.id.statisticsFragment,
                R.id.historyFragment, R.id.profileFragment -> {
                    toolbar_main.visibility = View.VISIBLE
                    bottomnav_main.visibility = View.VISIBLE
                    fab_main_exerciseaction.visibility = View.VISIBLE
                    toolbar_main.menu.forEach { menuItem ->
                        menuItem.isVisible = true
                    }
                }
                R.id.cameraFragment -> {
                    toolbar_main.visibility = View.GONE
                    bottomnav_main.visibility = View.GONE
                    fab_main_exerciseaction.visibility = View.GONE
                }
                else -> {
                    toolbar_main.visibility = View.VISIBLE
                    bottomnav_main.visibility = View.GONE
                    fab_main_exerciseaction.visibility = View.GONE
                    toolbar_main.menu.forEach { menuItem ->
                        menuItem.isVisible = false
                    }
                }
            }
        }
        val topLevelDestinations = setOf(
            R.id.dashboardFragment,
            R.id.statisticsFragment,
            R.id.historyFragment,
            R.id.profileFragment
        )
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)
        bottomnav_main.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        fab_main_exerciseaction.setOnClickListener {
            BottomSheetExerciseAction().show(
                supportFragmentManager,
                BottomSheetExerciseAction::class.java.simpleName
            )
        }

        // reset the database if the build is for debug and not for production
        /*if (BuildConfig.DEBUG) {
            deleteDatabase(Constants.DB_NAME)
            sp.edit().putInt(Constants.DB_INITIALISED_VERSION, 0).apply()
        }*/

        // check if the db has been initialised on this app version before
        val appVersion = PackageInfoCompat
            .getLongVersionCode(packageManager.getPackageInfo(packageName, 0))
            .toInt()
        if (sp.getInt(Constants.DB_INITIALISED_VERSION, 0) < appVersion) {
            // if not, then create the db with the needed content before continuing the data loading
            viewModel.initDatabase(this).observe(this) {
                Log.d(TAG, "Initialised database")
                sp.edit().putInt(Constants.DB_INITIALISED_VERSION, appVersion).apply()
            }
        }

        // log the exercise templates that are on the db
        viewModel.getAllExerciseTemplates().observe(this) { exerciseTemplates ->
            var output = ""
            exerciseTemplates.forEach {
                output += "\nname: ${it.name} equipment: ${it.equipment} bodyPart: ${it.bodyPart}"
            }
            Log.d(TAG, "Found ${exerciseTemplates.size} exercise templates: $output")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
                startEquipmentChooser(true)
            } else {
                // permission denied
                Snackbar.make(
                    constraintlayout_main,
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
            R.id.menu_app_bar_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_app_bar_logout -> {
                logout(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBottomSheetResult(useQR: Boolean) {
        if (useQR) {
            // check if the permission to use the camera has been granted
            val status = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (status == PackageManager.PERMISSION_GRANTED) {
                // permission has already been granted
                startEquipmentChooser(useQR)
            } else {
                // request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    Constants.PERMISSION_REQUEST_CODE_CAMERA
                )
            }
        } else {
            startEquipmentChooser(useQR)
        }
    }

    fun logout(displayMsg: Boolean) {
        if (displayMsg) {
            displayToast(getString(R.string.main_relogin_message))
        }
        sp.edit()
            .remove(Constants.USERNAME)
            .remove(Constants.JWT)
            .remove(Constants.USER_ID)
            .remove(Constants.ENCRYPTED_PWD)
            .remove(Constants.INITIALIZATION_VECTOR)
            .apply()
        startAuthActivity()
    }

    private fun displayToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun startEquipmentChooser(useQR: Boolean) {
        val dest: Int
        val options: NavOptions?
        if (useQR) {
            dest = R.id.cameraFragment
            options = null
        } else {
            dest = R.id.equipmentListFragment
            options = navOptions {
                anim {
                    enter = R.anim.slide_in_top
                    exit = R.anim.slide_out_bottom
                    popEnter = R.anim.slide_in_bottom
                    popExit = R.anim.slide_out_top
                }
            }
        }
        navController.navigate(dest, null, options)
    }

    private fun startAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
