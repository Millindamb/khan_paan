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
import com.example.gharkakhana.MenuBootemSheetFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBootemSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.fast_food_slider, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.pizza_sliders, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.indian_thali, ScaleTypes.FIT))

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
        val foodName= listOf("Maxican Burger","Paneer Pizza","Paneer Tikka","Hara bhara kabab","White sauce pasta","Paneer masala")
        val price= listOf("150Rs","279Rs","350Rs","249Rs","150Rs","249Rs")
        val popularFoodImages= listOf(
            R.drawable.burger_cart,
            R.drawable.pizza_cart,
            R.drawable.paneer_tikka_cart,
            R.drawable.hara_bhara_kabab_cart,
            R.drawable.white_sauce_pasta,
            R.drawable.paneer_masala_cart
        )
        val adapter= PopularAdapter(foodName,price,popularFoodImages)
        binding.popularRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.popularRecyclerView.adapter=adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}