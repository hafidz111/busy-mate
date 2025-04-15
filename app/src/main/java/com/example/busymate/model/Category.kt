package com.example.busymate.model

import com.example.busymate.R

data class Category (
    val textCategory: Int
)

val dummyCategory = listOf(
    Category(R.string.tag_all),
    Category(R.string.tag_food),
    Category(R.string.tag_craft),
    Category(R.string.tag_fashion),
    Category(R.string.tag_culinary),
    Category(R.string.tag_halal)
)