package com.example.uberride.ui.landing.bottomsheets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.uberride.data.model.CabData
import com.example.uberride.databinding.BottomSheetNearbyCabListBinding
import com.example.uberride.ui.landing.LandingViewModel
import com.example.uberride.ui.landing.adapters.NearbyCabListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NearbyCabListBottomSheet(private var list: ArrayList<CabData>) :
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
        serviceObserver()


    }

    /** Network Call */
    private fun bookMyCab() {
        lifecycleScope.launch {
            landingViewModel.bookMyCabServiceCall()
        }
    }


    //Initializes the cab list adapter and recycler view
    private fun initCabListRecyclerView() {
        nearbyCabListAdapter = NearbyCabListAdapter(this, list)
        binding.cabListRecyclerView.adapter = nearbyCabListAdapter
        nearbyCabListAdapter!!.notifyDataSetChanged()

    }

    private fun onClick() {
        binding.btnBookCab.setOnClickListener {
            bookMyCab()
        }
    }

    private fun serviceObserver() {

        landingViewModel.responseBookMyCab.observe(viewLifecycleOwner) { result->

            if (result != null) {
                Log.d("BOOKING", "${result.body()?.status}")
            }
        }
    }





    override fun bookCab(data: CabData, position: Int) {
        landingViewModel.carId = data.id
        if (data.isSelected) {
            binding.btnBookCab.isEnabled = true
        }
    }


}