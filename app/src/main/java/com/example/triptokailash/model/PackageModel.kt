package com.example.triptokailash.model

data class PackageModel(
    var packageId: String? = null,
    var packageName: String? = null,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val duration: String? = null,
    val imageUrl: String? = null,
    val category: String? = null,
    val itinerary: List<ItineraryDay>? = null,
    val inclusions: List<String>? = null,
    val exclusions: List<String>? = null
)

data class ItineraryDay(
    val day: Int? = null,
    val title: String? = null,
    val description: String? = null
)
