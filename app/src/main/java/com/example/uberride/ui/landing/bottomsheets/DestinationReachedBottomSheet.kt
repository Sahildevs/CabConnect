package com.example.uberride.ui.landing.bottomsheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uberride.R
import com.example.uberride.databinding.BottomSheetDestinationReachedBinding


class DestinationReachedBottomSheet : Fragment() {

    lateinit var binding: BottomSheetDestinationReachedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetDestinationReachedBinding.inflate(inflater, container, false)
        return binding.root
    }


}