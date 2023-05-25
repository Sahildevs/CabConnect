package com.example.uberride.ui.landing.bottomsheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uberride.R
import com.example.uberride.databinding.BottomSheetBookedCabDetailsBinding


class BookedCabDetailsBottomSheet : Fragment() {

    lateinit var binding: BottomSheetBookedCabDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetBookedCabDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


}