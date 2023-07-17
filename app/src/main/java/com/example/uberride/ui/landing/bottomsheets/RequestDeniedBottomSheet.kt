package com.example.uberride.ui.landing.bottomsheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.example.uberride.R
import com.example.uberride.databinding.BottomSheetRequestDeniedBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class RequestDeniedBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetRequestDeniedBinding
    private lateinit var animationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetRequestDeniedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showAnim()
    }

    private fun showAnim() {
        animationView = binding.animDenied
        animationView.setAnimation("denied.json")
        animationView.playAnimation()
    }


}