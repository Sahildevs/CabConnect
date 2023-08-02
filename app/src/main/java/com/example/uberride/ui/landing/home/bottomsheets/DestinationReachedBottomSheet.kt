package com.example.uberride.ui.landing.home.bottomsheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.example.uberride.R
import com.example.uberride.databinding.BottomSheetDestinationReachedBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DestinationReachedBottomSheet(private val callback: Callback) : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetDestinationReachedBinding
    lateinit var animationView: LottieAnimationView

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showAnim()

        binding.btnFinishTrip.setOnClickListener {
            callback.finishTrip()
        }

    }

    private fun showAnim() {
        animationView = binding.animFlag
        animationView.setAnimation("flag.json")
        animationView.playAnimation()

    }

    interface Callback{
        fun finishTrip()
    }

}