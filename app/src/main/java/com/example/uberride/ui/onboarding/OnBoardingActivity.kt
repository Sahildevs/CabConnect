package com.example.uberride.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.uberride.R
import com.example.uberride.databinding.ActivityMainBinding
import com.example.uberride.databinding.ActivityOnboardingBinding
import com.example.uberride.ui.landing.LandingBaseActivity
import com.example.uberride.ui.onboarding.bottomsheets.NetworkConnectionBottomSheet
import com.example.uberride.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity(), NetworkUtils.NetworkCallback {

    lateinit var binding: ActivityOnboardingBinding
    lateinit var navController: NavController
    lateinit var networkUtils: NetworkUtils
    private var networkConnectionBottomSheet: NetworkConnectionBottomSheet? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialized network utils class
        networkUtils = NetworkUtils(this)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        auth = FirebaseAuth.getInstance() //Initialised firebase auth

        //val toolbar = binding.appbarLayout.toolbar

        //Retrieve nav controller from nav host fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container1) as NavHostFragment
        navController = navHostFragment.navController


        //Setting the tool bar in sync with the nav graph
        //setSupportActionBar(toolbar)
        //setupActionBarWithNavController(navController)
    }

    //For the up button
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }


    override fun onStart() {
        super.onStart()


    }

    override fun onResume() {
        super.onResume()

        //Register the broadcast receiver to listen for network state
        networkUtils.registerBroadcastReceiver(this, networkUtils)

    }

    override fun onPause() {
        super.onPause()

        //Unregister broadcast receiver
        networkUtils.unRegisterBroadcastReceiver(this, networkUtils)
    }


    private fun showConnectionBottomSheet() {
        networkConnectionBottomSheet = NetworkConnectionBottomSheet()
        networkConnectionBottomSheet!!.show(supportFragmentManager, null)
        networkConnectionBottomSheet!!.isCancelable = false

    }

    override fun networkState(available: Boolean) {

        if (!available) {
            showConnectionBottomSheet()
        }
        else {
            networkConnectionBottomSheet?.dismiss()
        }
    }
}