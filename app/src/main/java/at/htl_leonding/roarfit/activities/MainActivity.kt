package at.htl_leonding.roarfit.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.htl_leonding.roarfit.R
import at.htl_leonding.roarfit.utils.Constants
import at.htl_leonding.roarfit.viewmodels.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var sharedModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Setup navigation
        val navController = findNavController(this, R.id.nav_host_fragment)
        bottom_nav.setupWithNavController(navController)

        // Setup configuration with top-level destinations
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.dashboardFragment,
            R.id.statisticsFragment,
            R.id.historyFragment,
            R.id.profileFragment
        ).build()
        setupActionBarWithNavController(navController, appBarConfiguration)

        fab.setOnClickListener {
            // Check if the permission to use the camera has been granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission has not been granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    Constants.PERMISSION_REQUEST_CODE_CAMERA
                )
            } else {
                // Permission has already been granted
                startCameraActivity()
            }
        }

        loadUser()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_REQUEST_CODE_CAMERA) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
                startCameraActivity()
            } else {
                // Permission denied
                Snackbar.make(
                    findViewById(R.id.main_constraint_layout),
                    "Please allow the needed permissions to use this feature",
                    Snackbar.LENGTH_LONG
                ).setAction("Action", null).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    private fun loadUser() {
        val sp = getSharedPreferences(Constants.PREFERENCE_FILE, Context.MODE_PRIVATE)
        val jwt = sp.getString("jwt", null)
        val customerNum = sp.getInt("customer_num", -1)
        if (jwt != null && customerNum != -1) {
            sharedModel.getUser(jwt, customerNum)
        }
    }

    private fun startAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

}
