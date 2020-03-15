package at.spiceburg.roarfit.features.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
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
import at.spiceburg.roarfit.data.NetworkError
import at.spiceburg.roarfit.features.auth.AuthActivity
import at.spiceburg.roarfit.features.main.statistics.StatisticsViewModel
import at.spiceburg.roarfit.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomSheetExerciseAction.ClickListener {

    lateinit var sp: SharedPreferences
    lateinit var progressMain: ContentLoadingProgressBar
    private lateinit var viewModel: MainViewModel
    private lateinit var statisticsViewModel: StatisticsViewModel
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        progressMain = findViewById(R.id.progress_main)
        progressMain.hide()

        // check if jwt is available
        val jwt: String? = sp.getString(Constants.JWT, null)
        if (jwt == null) {
            displaySnackbar(getString(R.string.networkerror_jwt_expired))
            logout()
        } else {
            // setup main viewModel
            val appContainer = (application as MyApplication).appContainer
            val mainViewModelFactory = MainViewModel.Factory(
                jwt,
                appContainer.userRepository,
                appContainer.workoutRepository
            )
            viewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

            // setup statistics viewModel
            val statisticsViewModelFactory = StatisticsViewModel.Factory(
                jwt,
                appContainer.workoutRepository
            )
            statisticsViewModel = ViewModelProvider(
                this,
                statisticsViewModelFactory
            ).get(StatisticsViewModel::class.java)

            // setup navigation
            setSupportActionBar(toolbar_main)
            navController = findNavController(this, R.id.navhost_main)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.dashboardFragment,
                    R.id.historyFragment, R.id.profileFragment -> {
                        toolbar_main.visibility = View.VISIBLE
                        appbarlayout_main.elevation = 10.5f
                        bottomnav_main.visibility = View.VISIBLE
                        fab_main_exerciseaction.visibility = View.VISIBLE
                        toolbar_main.menu.forEach { menuItem ->
                            menuItem.isVisible = true
                        }
                    }
                    R.id.statisticsFragment -> {
                        toolbar_main.visibility = View.VISIBLE
                        appbarlayout_main.elevation = 0f
                        bottomnav_main.visibility = View.VISIBLE
                        fab_main_exerciseaction.visibility = View.INVISIBLE
                        toolbar_main.menu.forEach { menuItem ->
                            menuItem.isVisible = true
                        }
                    }
                    R.id.cameraFragment -> {
                        toolbar_main.visibility = View.GONE
                        appbarlayout_main.elevation = 10.5f
                        bottomnav_main.visibility = View.GONE
                        fab_main_exerciseaction.visibility = View.GONE
                    }
                    else -> {
                        toolbar_main.visibility = View.VISIBLE
                        appbarlayout_main.elevation = 10.5f
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
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadWorkoutPlans()
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
            R.id.menu_app_bar_logout -> {
                logout()
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

    fun handleNetworkError(errorType: NetworkError) {
        when (errorType) {
            NetworkError.SERVER_UNREACHABLE -> displaySnackbar(getString(R.string.networkerror_server_unreachable))
            NetworkError.TIMEOUT -> displaySnackbar(getString(R.string.networkerror_timeout))
            NetworkError.JWT_EXPIRED -> {
                displayToast(getString(R.string.networkerror_jwt_expired))
                logout()
            }
            else -> {
                displayToast(getString(R.string.networkerror_unexpected))
                logout()
            }
        }
    }

    private fun logout() {
        sp.edit()
            .remove(Constants.USERNAME)
            .remove(Constants.JWT)
            .remove(Constants.ENCRYPTED_PWD)
            .remove(Constants.INITIALIZATION_VECTOR)
            .remove(Constants.DONT_REMIND_BIOMETRIC)
            .apply()
        startAuthActivity()
    }

    private fun displaySnackbar(text: String) {
        Snackbar.make(constraintlayout_main, text, Snackbar.LENGTH_LONG)
            .setAction("Dismiss") {}
            .show()
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
