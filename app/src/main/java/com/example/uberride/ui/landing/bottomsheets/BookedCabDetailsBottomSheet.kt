package com.example.uberride.ui.landing.bottomsheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.uberride.R
import com.example.uberride.databinding.BottomSheetBookedCabDetailsBinding
import com.example.uberride.ui.landing.LandingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BookedCabDetailsBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetBookedCabDetailsBinding

    private val landingViewModel: LandingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetBookedCabDetailsBinding.inflate(inflater, container, false)
        binding.apply {
            viewmodel = landingViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this).load(landingViewModel.carImage?.toUri()).placeholder(R.drawable.ic_launcher_background).into(binding.carImage)

        binding.btnHideDetails.setOnClickListener {
            this.dismiss()
        }
    }


}