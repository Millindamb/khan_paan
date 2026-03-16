package com.example.gharkakhana.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.gharkakhana.R
import com.example.gharkakhana.adapter.PopularAdapter
import com.example.gharkakhana.databinding.FragmentHomeBinding
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

        // Image Slider
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.fast_food_slider, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.pizza_sliders, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.indian_thali, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object : ItemClickListener {

            override fun doubleClick(position: Int) {}

            override fun onItemSelected(position: Int) {
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })


        // Food Data
        val foodName = listOf(
            "Maxican Burger",
            "Paneer Pizza",
            "Paneer Tikka",
            "Hara bhara kabab",
            "White sauce pasta",
            "Paneer masala",
            "Toar tadka daal",
            "Hakka noodles",
            "Rasmalai",
            "Butter naan",
            "Veg biryani",
            "Chole khulche",
            "Hakka noodles",
            "Gulab jamun",
            "Bhindi masala",
            "Manchurian dry",
            "Masala dhosa",
            "Daal makhani"
        )

        val price = listOf(
            "150Rs",
            "279Rs",
            "350Rs",
            "249Rs",
            "150Rs",
            "249Rs",
            "229Rs",
            "189Rs",
            "249Rs",
            "199Rs",
            "249Rs",
            "229Rs",
            "189Rs",
            "229Rs",
            "199Rs",
            "179s",
            "160Rs",
            "199Rs"
        )

        val popularFoodImages = listOf(
            R.drawable.burger_cart,
            R.drawable.pizza_cart,
            R.drawable.paneer_tikka_cart,
            R.drawable.hara_bhara_kabab_cart,
            R.drawable.white_sauce_pasta,
            R.drawable.paneer_masala_cart,
            R.drawable.toar_daal,
            R.drawable.hakka_noodles,
            R.drawable.rasmalai,
            R.drawable.butter_naan,
            R.drawable.veg_biryani,
            R.drawable.chole_khulche,
            R.drawable.hakka_noodles,
            R.drawable.gulab_jamun,
            R.drawable.bhindi_masala,
            R.drawable.manchurian,
            R.drawable.Xhosa,
            R.drawable.daal_makhani

        )

        // Adapter
        val adapter = PopularAdapter(
            requireContext(),
            foodName,
            price,
            popularFoodImages
        )

        // GRID LAYOUT (2 cards per row)
        binding.popularRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.popularRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}