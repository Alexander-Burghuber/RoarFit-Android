package at.spiceburg.roarfit.features.main

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.spiceburg.roarfit.MyApplication
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.features.auth.AuthActivity
import at.spiceburg.roarfit.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomSheetExerciseAction.BottomSheetListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appContainer = (application as MyApplication).appContainer
        viewModel = ViewModelProviders.of(this, appContainer.mainViewModelFactory)
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
            val bottomSheet = BottomSheetExerciseAction()
            bottomSheet.show(
                supportFragmentManager,
                BottomSheetExerciseAction::class.java.simpleName
            )
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
            viewModel.initDatabase(this).observe(this, Observer {
                Log.d(TAG, "Initialised database")
                sp.edit().putInt("db_initialised_version", appVersion).apply()
            })
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
            R.id.menu_app_bar_settings -> true
            R.id.menu_app_bar_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
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

    private fun startEquipmentChooser(useQR: Boolean) {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_top //slide in right
                exit = R.anim.slide_out_bottom //slide out left
                popEnter = R.anim.slide_in_bottom
                popExit = R.anim.slide_out_top
            }
        }
        val dest = if (useQR) R.id.cameraFragment else R.id.equipmentListFragment
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
