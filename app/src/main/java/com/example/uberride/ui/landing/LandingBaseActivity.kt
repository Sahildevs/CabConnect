package com.example.uberride.ui.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
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

        retrieveBundle()

        //Customized status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.yellow)

        auth = FirebaseAuth.getInstance()

        //Retrieving NavController from the NavHost fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container2) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()

        //Defining top level destinations on which the action bar will be visible
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.landingMapsFragment), drawerLayout
        )

        //SETTING THE TOOLBAR IN SYNC WITH THE NAVIGATION GRAPH
        setSupportActionBar(actionbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //On hamburger clicked open the navigation drawer
        actionbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }


        //Nav drawer menu item listener
        navDrawer.setNavigationItemSelectedListener {
            when(it.itemId){

                R.id.explore -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    //drawerLayout.closeDrawer(Gravity.LEFT)
                }


                R.id.help -> {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
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


    //Retrieve the bundle passed from the onboarding activity
    private fun retrieveBundle() {
        val bundle = intent.extras

        if (bundle != null) {
            landingViewModel.userId = bundle.getInt("USER_ID")
            landingViewModel.name = bundle.getString("NAME")
            landingViewModel.phone = bundle.getString("PHONE")
        }

    }

    private fun showConnectionBottomSheet() {
        networkConnectionBottomSheet!!.show(supportFragmentManager, null)
        networkConnectionBottomSheet!!.isCancelable = false

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