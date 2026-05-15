package com.example.kalavidarabalaga

object TroupeData {
    val troupes = listOf(
        Troupe(
            id = "1",
            name = "Sri Vinayaka Dollu Kunitha",
            artType = "Dollu Kunitha",
            district = "Mandya",
            phone = "9876543210",
            description = "Traditional Dollu Kunitha group with 15 years of experience. We perform at weddings, festivals, and corporate events.",
            instruments = listOf("Dollu", "Talam", "Flute"),
            equipment = listOf("10 Drums", "Stage Sound System", "Traditional Costumes"),
            images = listOf(
                "sri_1",
                "sri_2",
                "sri_3"
            ),
            isVerified = true,
            basePrice = "₹15,000",
            availability = listOf("Mon", "Wed", "Fri", "Sat", "Sun")
        ),
        Troupe(
            id = "2",
            name = "Malnad Pooja Kunitha Team",
            artType = "Pooja Kunitha",
            district = "Shivamogga",
            phone = "9123456780",
            description = "Authentic Pooja Kunitha performers from the heart of Malnad. Known for our precise movements and vibrant energy.",
            instruments = listOf("Nadaswaram", "Thavil"),
            equipment = listOf("Decorative Props", "Folk Instruments"),
            images = listOf(
                "malnad_1",
                "malnad_2",
                "malnad_3"
            ),
            isVerified = true,
            basePrice = "₹12,000",
            availability = listOf("Sat", "Sun")
        ),
        Troupe(
            id = "3",
            name = "Coastal Yakshagana Mandali",
            artType = "Yakshagana",
            district = "Udupi",
            phone = "9988776655",
            description = "Bringing the divine art of Yakshagana to your stage. We specialize in mythological plays (Prasangas).",
            instruments = listOf("Chende", "Maddale", "Sruti"),
            equipment = listOf("Elaborate Headgears", "Full Makeup Kit", "Backdrop Curtains"),
            images = listOf(
                "https://karnatakatourism.org/wp-content/uploads/2020/06/Yakshagana-1.jpg",
                "dollu_5"
            ),
            isVerified = false,
            basePrice = "₹25,000",
            availability = listOf("Daily")
        ),
        Troupe(
            id = "4",
            name = "Nritya Sudha Bharatanatyam",
            artType = "Bharatanatyam",
            district = "Bengaluru",
            phone = "9880011223",
            description = "Exquisite Bharatanatyam performances blending tradition with grace. Suitable for corporate events and classical festivals.",
            instruments = listOf("Mridangam", "Violin", "Flute"),
            equipment = listOf("Standard Stage", "Professional Audio Setup"),
            images = listOf("bar_1", "bar_2","bar_3"),
            isVerified = true,
            basePrice = "₹20,000",
            availability = listOf("Mon", "Fri", "Sat", "Sun")
        ),
        Troupe(
            id = "5",
            name = "Goravara Kunitha Sangha",
            artType = "Goravara Kunitha",
            district = "Mysuru",
            phone = "9448855221",
            description = "Devotional dance form dedicated to Lord Mailara Linga. Famous for our distinct black costumes and bear-skin caps.",
            instruments = listOf("Damaru", "Flute"),
            equipment = listOf("Traditional Black Costumes", "Bear-skin Caps"),
            images = listOf(
                "goravara_1",
                "goravara_2",
            ),
            isVerified = true,
            basePrice = "₹10,000",
            availability = listOf("Tue", "Thu", "Sat", "Sun")
        ),
        Troupe(
            id = "6",
            name = "Royal Wedding Melodies",
            artType = "Wedding Musical Band",
            district = "Mysuru",
            phone = "9449988776",
            description = "Dynamic musical band specializing in folk and cinematic wedding music. We bring the celebration to life.",
            instruments = listOf("Trumpet", "Drums", "Saxophone", "Keyboard"),
            equipment = listOf("Complete PA System", "Stage Lighting"),
            images = listOf("troup_1", "troup_2"),
            isVerified = true,
            basePrice = "₹35,000",
            availability = listOf("Daily")
        )
    )
}
