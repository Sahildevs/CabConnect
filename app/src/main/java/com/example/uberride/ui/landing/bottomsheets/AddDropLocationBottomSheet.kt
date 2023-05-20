package com.example.uberride.ui.landing.bottomsheets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uberride.R
import com.example.uberride.databinding.BottomsheetAddDropLocationBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddDropLocationBottomSheet(private var callback: Callback) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetAddDropLocationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomsheetAddDropLocationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {

            val dropLocation = binding.etDropLocation.text.trim().toString()

            if (dropLocation.isNotEmpty()) {
                callback.searchDropLocation(dropLocation)
            }
            else {
                Toast.makeText(requireContext(), "Please provide a drop location", Toast.LENGTH_SHORT).show()
            }

        }

    }

    interface Callback {
        fun searchDropLocation(dropLocation: String)
    }

}