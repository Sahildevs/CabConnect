package com.example.uberride.ui.landing.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uberride.data.model.CabData
import com.example.uberride.databinding.CellNearbyCabsBinding


class NearbyCabListAdapter(private var callback: Callback, private var cabList: ArrayList<CabData>):
    RecyclerView.Adapter<NearbyCabListAdapter.ViewHolder>(){



    class ViewHolder(
        private val binding: CellNearbyCabsBinding,
        listener: Callback
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(data: CabData) {

            binding.data = data
            binding.executePendingBindings()
        }

        init {
            binding.parentLayout.setOnClickListener {
                binding.data?.let { data ->

                    //Toggle between the selected state
                    data.isSelected = !data.isSelected
                    binding.parentLayout.isSelected = data.isSelected

                    //If any cab is selected, enable the send request button
                    listener.selectedCab(data, adapterPosition)

                }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CellNearbyCabsBinding.inflate(LayoutInflater.from(parent.context)), callback)
    }

    override fun getItemCount(): Int {
        return cabList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cabList[position])
    }

    interface Callback{
        fun selectedCab(data: CabData, position: Int)
    }

}