package com.example.kalavidarabalaga

data class Troupe(
    val id: String,
    val name: String,
    val artType: String,
    val district: String,
    val phone: String,
    val description: String,
    val instruments: List<String>,
    val equipment: List<String>,
    val images: List<String>, // Changed to String to support both local resource names and URLs
    val isVerified: Boolean = false,
    val basePrice: String = "Contact for Quote",
    val availability: List<String> = listOf("Weekends", "Public Holidays")
)
