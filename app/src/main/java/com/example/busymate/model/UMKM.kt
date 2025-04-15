package com.example.busymate.model

import androidx.annotation.StringRes
import com.example.busymate.R

data class UMKM(
    val id: Int,
    val image: String,
    @StringRes val name: Int,
    val category: Int,
    @StringRes val location: Int,
    val tags: List<Int> = emptyList(),
    val description: String,
    val menus: List<MenuItem>,
    val contact: List<String>
)

val dummyUMKM = listOf(
    UMKM(
        id = 1,
        image = "https://asset.kompas.com/crop/0x23:1040x716/750x500/data/photo/2019/09/10/5d769c50bf324.jpeg",
        name = R.string.umkm_barokah,
        category = R.string.category_makanan,
        location = R.string.location_jakarta,
        tags = listOf(R.string.tag_culinary, R.string.tag_halal),
        description = "Ini adalah deskripsi dari UMKM ini, yang menjual makanan enak.",
        menus = dummyMenus,
        contact = listOf("081234567890", "contact@umkm.com")
    ),
    UMKM(
        id = 2,
        image = "https://yogyaku.com/wp-content/uploads/2023/05/Batik-sebagai-warisan-budaya-Indonesia-630x380.jpg",
        name = R.string.umkm_batik_modern,
        category = R.string.category_fashion,
        location = R.string.location_bandung,
        tags = listOf(R.string.tag_batik, R.string.tag_modern),
        description = "Deskripsi tentang UMKM yang menjual pakaian.",
        menus = dummyMenus,
        contact = listOf("082345678901", "info@umkm.com")
    )
)
