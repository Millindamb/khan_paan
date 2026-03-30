package com.example.gharkakhana.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
    @SerialName("id")               val id: String? = null,
    @SerialName("food_name")        val foodName: String? = null,
    @SerialName("food_price")       val foodPrice: String? = null,
    @SerialName("food_description") val foodDescription: String? = null,
    @SerialName("food_image_url")       val foodimgurl: String? = null,
    @SerialName("food_ingredient")  val foodIngredients: String? = null
)