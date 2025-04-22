package com.example.busymate.model

data class UMKM(
    val id: Int,
    val imageUMKM: String,
    val nameUMKM: String,
    val category: String,
    val location: String,
    val tags: List<Int> = emptyList(),
    val description: String,
    val products: List<ProductItem>,
    val contact: String
)
