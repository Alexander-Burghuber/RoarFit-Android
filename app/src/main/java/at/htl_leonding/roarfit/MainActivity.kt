package at.htl_leonding.roarfit

import android.Manifest
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.htl_leonding.roarfit.utils.Constants
import at.htl_leonding.roarfit.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        model = ViewModelProviders.of(this).get(MainViewModel::class.java)

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

        // Check the authorization
        val am = AccountManager.get(this)
        val accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE)
        if (accounts.isNotEmpty()) {
            val account = accounts[0]
            am.getAuthToken(
                account,
                "full_access",
                null,
                this,
                OnTokenAcquired(),
                null
            )
        } else {
            startAuthActivity()
        }

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
                val am = AccountManager.get(this)
                val accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE)
                if (accounts.isNotEmpty()) {
                    val account = accounts[0]
                    am.removeAccount(
                        account,
                        this,
                        {
                            startAuthActivity()
                        },
                        null
                    )
                } else {
                    startAuthActivity()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    private inner class OnTokenAcquired : AccountManagerCallback<Bundle> {
        override fun run(future: AccountManagerFuture<Bundle>) {
            val bundle = future.result
            val token = bundle.getString(AccountManager.KEY_AUTHTOKEN)
            main_constraint_layout.visibility = View.VISIBLE
        }
    }

}
