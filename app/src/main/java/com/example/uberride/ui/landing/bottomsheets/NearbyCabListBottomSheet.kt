package com.example.uberride.ui.landing.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.uberride.data.model.CabData
import com.example.uberride.databinding.BottomSheetNearbyCabListBinding
import com.example.uberride.ui.landing.LandingViewModel
import com.example.uberride.ui.landing.adapters.NearbyCabListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NearbyCabListBottomSheet(private val callback: Callback,private var list: ArrayList<CabData>) :
    BottomSheetDialogFragment(), NearbyCabListAdapter.Callback {

    lateinit var binding: BottomSheetNearbyCabListBinding
    private var nearbyCabListAdapter: NearbyCabListAdapter? = null

    private val landingViewModel: LandingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = BottomSheetNearbyCabListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCabListRecyclerView()
        onClick()



    }



    //Initializes the cab list adapter and recycler view
    private fun initCabListRecyclerView() {
        nearbyCabListAdapter = NearbyCabListAdapter(this, list)
        binding.cabListRecyclerView.adapter = nearbyCabListAdapter
        nearbyCabListAdapter!!.notifyDataSetChanged()

    }

    private fun onClick() {

        binding.btnBookCab.setOnClickListener {
            callback.requestCab()
        }
    }


    //Stores selected cab details to view model
    override fun selectedCab(data: CabData, position: Int) {
        landingViewModel.carId = data.id
        landingViewModel.driverId = data.drivers_id
        landingViewModel.driverName = data.driver.name
        landingViewModel.driverPhone = data.driver.phone
        landingViewModel.carModel = data.model
        landingViewModel.numberPlate = data.numberPlate
        if (data.isSelected) {
            binding.btnBookCab.isEnabled = true
        }
    }

    interface Callback {
        fun requestCab()
    }


}