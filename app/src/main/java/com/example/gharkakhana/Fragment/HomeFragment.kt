package com.example.gharkakhana.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.gharkakhana.R
import com.example.gharkakhana.adapter.PopularAdapter
import com.example.gharkakhana.databinding.FragmentHomeBinding
import androidx.recyclerview.widget.LinearLayoutManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.banner1_foreground, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2_foreground, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3_foreground, ScaleTypes.FIT))

        val imageSlider=binding.imageSlider
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object: ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition=imageList[position]
                val itemMessage="Selected Image $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
            }

        })
        val foodName= listOf("Burger","Pizza","Hotdog","momo")
        val price= listOf("$5","$8","$6","$5")
        val popularFoodImages= listOf(
            R.drawable.food4_background,
            R.drawable.food3_background,
            R.drawable.food2_background,
            R.drawable.food1_background
        )
        val adapter= PopularAdapter(foodName,price,popularFoodImages)
        binding.PopularRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter=adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}