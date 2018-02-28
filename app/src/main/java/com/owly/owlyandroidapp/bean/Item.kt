package com.owly.owlyandroidapp.bean
data class Item(
        val itemId: Int,
        val parentItemId: Int,
        val name: String,
        val salePrice: Double,
        val shortDescription: String,
        val thumbnailImage: String,
        val mediumImage: String,
        val largeImage: String)
