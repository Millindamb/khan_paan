package com.example.gharkakhana.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gharkakhana.R
import com.example.gharkakhana.adapter.CartAdapter
import com.example.gharkakhana.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCartBinding.inflate(inflater,container,false)

        val cartFoodName= listOf("Maxican Burger","Paneer Pizza","White sauce pasta")
        val cartItemPrice= listOf("150Rs","279Rs","150Rs")
        val cartImage= listOf(R.drawable.burger_cart,R.drawable.pizza_cart,R.drawable.white_sauce_pasta)
        val adapter= CartAdapter(ArrayList(cartFoodName), ArrayList(cartItemPrice), ArrayList(cartImage))

        binding.cartRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter=adapter

        return binding.root
    }

    companion object {
    }
}