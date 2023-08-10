package com.example.uberride.ui.landing


import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.uberride.R
import com.example.uberride.databinding.ActivityLandingBaseBinding
import com.example.uberride.ui.onboarding.OnBoardingActivity
import com.example.uberride.ui.onboarding.bottomsheets.NetworkConnectionBottomSheet
import com.example.uberride.utils.NetworkUtils
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LandingBaseActivity : AppCompatActivity(), NetworkUtils.NetworkCallback {

    lateinit var binding: ActivityLandingBaseBinding
    lateinit var networkUtils: NetworkUtils

    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var auth: FirebaseAuth

    private var networkConnectionBottomSheet: NetworkConnectionBottomSheet? = null

    //ACCESSING VIEWS FROM LAYOUT
    private val actionbar : MaterialToolbar
    get() = binding.topAppBar

    private val drawerLayout : DrawerLayout
    get() = binding.drawerLayout

    private val navDrawer : NavigationView
    get() = binding.navDrawer

    //back press timer
    var backPressedTime: Long = 0


    private val landingViewModel: LandingViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialized network utils
        networkUtils = NetworkUtils(this)

        networkConnectionBottomSheet = NetworkConnectionBottomSheet()

        retrieveUserDataFromSharedPref()

        //Customized status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.custom_status)

        auth = FirebaseAuth.getInstance()

        //Retrieving NavController from the NavHost fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container2) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()

        //Defining top level destinations on which the action bar with menu will be visible
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.landingMapsFragment), drawerLayout
        )

        //SETTING THE TOOLBAR IN SYNC WITH THE NAVIGATION GRAPH
        setSupportActionBar(actionbar)
        setupActionBarWithNavController(navController, appBarConfiguration)



//        //On hamburger clicked open the navigation drawer
//        actionbar.setNavigationOnClickListener {
//            drawerLayout.openDrawer(Gravity.LEFT)
//        }


        //Nav drawer menu item listener
        navDrawer.setNavigationItemSelectedListener {
            when(it.itemId){

                R.id.profile -> {
                    //Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_landingMapsFragment_to_userProfileFragment)
                    drawerLayout.closeDrawer(Gravity.LEFT)
                }


                R.id.logout -> {
                    //Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()

                    landingViewModel.clearSharedPreData()
                    auth.signOut()

                    startActivity(Intent(this, OnBoardingActivity::class.java))
                    finish()
                }


            }
            true
        }
    }

    override fun onResume() {
        super.onResume()

        //Register broadcast receiver to listen network state
        networkUtils.registerBroadcastReceiver(this, networkUtils)
    }

    override fun onPause() {
        super.onPause()

        //Unregister broadcast receiver
        networkUtils.unRegisterBroadcastReceiver(this, networkUtils)
    }


    //Retrieve the user data stored in shared preference
    private fun retrieveUserDataFromSharedPref() {
        landingViewModel.getDataFromSharedPreferences()

    }

    private fun showConnectionBottomSheet() {
        networkConnectionBottomSheet!!.show(supportFragmentManager, null)
        networkConnectionBottomSheet!!.isCancelable = false

    }


    //This method is responsible for handling the navigation to the appropriate parent destination when the "up" button is pressed.
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    //When user presses back
    override fun onBackPressed() {

        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity()
        } else {
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()

    }


    override fun networkState(available: Boolean) {

        val isBottomSheetOpen = networkConnectionBottomSheet!!.isAdded && !networkConnectionBottomSheet!!.isHidden

        if (!available && !isBottomSheetOpen) {
            showConnectionBottomSheet()
        }
        else if (available && isBottomSheetOpen) {
            //Dismiss bottom sheet only if the network is available and bottom sheet is not hidden
            networkConnectionBottomSheet!!.dismiss()
        }


    }
}