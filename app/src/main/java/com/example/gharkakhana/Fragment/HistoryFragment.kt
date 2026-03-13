package com.example.gharkakhana.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.R
import com.example.gharkakhana.adapter.BuyAgainAdapter
import com.example.gharkakhana.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHistoryBinding.inflate(inflater,container,false)
        setuprecycularview()
        return binding.root
    }

    private fun setuprecycularview(){
        val buyAgainFoodName= listOf("Maxican Burger","Paneer Pizza","Paneer Tikka","White sauce pasta")
        val buyAgainFoodPrice= listOf("150Rs","279Rs","350Rs","150Rs")
        val buyAgainFoodImage= listOf(
            com.example.gharkakhana.R.drawable.burger_cart,
            com.example.gharkakhana.R.drawable.pizza_cart,
            com.example.gharkakhana.R.drawable.paneer_tikka_cart,
            com.example.gharkakhana.R.drawable.white_sauce_pasta
        )
        buyAgainAdapter= BuyAgainAdapter(buyAgainFoodName,buyAgainFoodPrice,buyAgainFoodImage)
        binding.BuyAgainRecyclerView.adapter=buyAgainAdapter
        binding.BuyAgainRecyclerView.layoutManager= LinearLayoutManager(requireContext())
    }

    companion object {
    }
}