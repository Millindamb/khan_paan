package com.example.gharkakhana

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.gharkakhana.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailsBinding
    private var foodName: String?=null
    private var foodImage: String?=null
    private var foodPrice: String?=null
    private var foodDescription: String?=null
    private var foodIngredients: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        foodName=intent.getStringExtra("MenuItemName")
        foodPrice=intent.getStringExtra("MenuItemPrice")
        foodDescription=intent.getStringExtra("MenuItemDescription")
        foodIngredients=intent.getStringExtra("MenuItemIngredients")
        foodImage=intent.getStringExtra("MenuItemImage")

        with(binding){
            detailFoodName.text=foodName
            detailFoodPrice.text=foodPrice
            descriptionTextView.text=foodDescription
            ingrediantsTextView.text=foodIngredients
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(detailFoodImage)
        }

        binding.detailBackButton.setOnClickListener {
            finish()
        }
    }
}