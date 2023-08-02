package com.example.uberride.ui.landing.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.uberride.R
import com.example.uberride.databinding.FragmentUserProfileBinding
import com.example.uberride.ui.landing.LandingViewModel


class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding

    private val landingViewModel: LandingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        binding.apply {
            viewmodel = landingViewModel
        }
        return binding.root

    }



}