package com.example.busymate.model

data class UMKM(
    val id: String = "",
    val imageUMKM: String = "",
    val nameUMKM: String = "",
    val contact: String = "",
    val location: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val description: String = "",
    val products: List<ProductItem> = emptyList()
)
