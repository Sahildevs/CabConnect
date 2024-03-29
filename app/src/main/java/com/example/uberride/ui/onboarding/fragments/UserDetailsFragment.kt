package com.example.uberride.ui.onboarding.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.uberride.R
import com.example.uberride.databinding.FragmentUserdetailsBinding
import com.example.uberride.ui.landing.LandingBaseActivity
import com.example.uberride.ui.onboarding.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {

    lateinit var binding: FragmentUserdetailsBinding
    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserdetailsBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = onboardingViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        serviceObserver()

    }


    /** Network Call */
    private fun addNewUser() {
        lifecycleScope.launch {
            onboardingViewModel.addNewUserServiceCall()
        }
    }



    private fun onClick() {

        binding.btnLogin.setOnClickListener {

            if (binding.etName.text.isNotEmpty() && binding.etPhone.text.isNotEmpty()){

                binding.loader.isVisible = true
                onboardingViewModel.name = binding.etName.text.toString()
                onboardingViewModel.phoneNumber = binding.etPhone.text.toString()

                addNewUser()

            }
            else {
                Toast.makeText(requireActivity(), "Please Enter Your Name", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun serviceObserver() {

        onboardingViewModel.responseAddNewUser.observe(viewLifecycleOwner) { result->
            if (result.body() != null) {
                //Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                binding.loader.isVisible = false

                onboardingViewModel.userId = result.body()!!.user.id
                onboardingViewModel.name = result.body()!!.user.name
                onboardingViewModel.phoneNumber = result.body()!!.user.phone

                storeDataToSharedPreference()
                navigateToLandingMain()


            }
            else {
                Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()

            }
        }
    }


    //Add user details to shared preference
    private fun storeDataToSharedPreference() {
        onboardingViewModel.addDataToSharedPref()

    }

    private fun navigateToLandingMain() {
        findNavController().navigate(R.id.action_userDetailsFragment_to_landingBaseActivity)
    }






}