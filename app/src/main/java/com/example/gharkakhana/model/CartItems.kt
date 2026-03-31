package com.example.gharkakhana.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItems(
    @SerialName("id")               val id: String? = null,
    @SerialName("user_id")          val userId: String? = null,
    @SerialName("food_name")        val foodName: String? = null,
    @SerialName("food_price")       val foodPrice: String? = null,
    @SerialName("food_description") val foodDescription: String? = null,
    @SerialName("food_image")       val foodImage: String? = null,
    @SerialName("food_quantity")    val foodQuantity: Int? = 1,
    @SerialName("food_ingredients") val foodIngredients: String? = null
)