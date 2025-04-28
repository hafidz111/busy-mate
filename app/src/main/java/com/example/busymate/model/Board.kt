package com.example.busymate.model

data class Board(
    var id: String = "",
    var description: String = "",
    var umkm: UMKM = UMKM(),
    var imageUrl: String = "",
    var isPrivate: Boolean = false,
    var timestamp: Long = 0L
)


