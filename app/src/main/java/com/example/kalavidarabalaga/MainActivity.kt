package com.example.kalavidarabalaga

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.kalavidarabalaga.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }

            KalavidaraBalagaTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("list", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Bookings : Screen("bookings", "Bookings", Icons.Filled.List, Icons.Outlined.List)
    object Offers : Screen("offers", "Offers", Icons.Filled.Star, Icons.Outlined.Star)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    object Account : Screen("account", "My Account", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AppNavigation(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarScreens = listOf(Screen.Home, Screen.Bookings, Screen.Offers, Screen.Settings, Screen.Account)
    val showBottomBar = bottomBarScreens.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomBarScreens.forEach { screen ->
                        val isSelected = currentDestination?.route == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (screen == Screen.Offers) {
                                            Badge(containerColor = DeepGreen) { Text("New", color = Color.White) }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) screen.icon else screen.unselectedIcon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) TraditionalRed else Color.Gray
                                    )
                                }
                            },
                            label = { 
                                Text(
                                    screen.title, 
                                    color = if (isSelected) TraditionalRed else Color.Gray,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                ) 
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController, 
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                TroupeListScreen(
                    onTroupeClick = { troupeId ->
                        navController.navigate("detail/$troupeId")
                    },
                    onChatClick = {
                        navController.navigate("chatbot")
                    }
                )
            }
            composable(Screen.Bookings.route) { RecentBookingsScreen() }
            composable(Screen.Offers.route) { SeasonalOffersScreen() }
            composable(Screen.Settings.route) { 
                SettingsScreen(isDarkTheme = isDarkTheme, onThemeChange = onThemeChange) 
            }
            composable(Screen.Account.route) { MyAccountScreen() }
            
            composable("chatbot") {
                ChatBotScreen(onBack = { navController.popBackStack() })
            }
            composable(
                "detail/{troupeId}",
                arguments = listOf(navArgument("troupeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val troupeId = backStackEntry.arguments?.getString("troupeId")
                val troupe = TroupeData.troupes.find { it.id == troupeId }
                if (troupe != null) {
                    TroupeDetailScreen(troupe = troupe, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
fun RecentBookingsScreen() {
    val mockBookings = listOf(
        BookingInfo("Sri Vinayaka Dollu Kunitha", "20 Oct 2024", "Wedding", "Confirmed", TraditionalRed),
        BookingInfo("Malnad Pooja Kunitha Team", "15 Oct 2024", "Corporate Event", "Completed", DeepGreen),
        BookingInfo("Coastal Yakshagana Mandali", "05 Oct 2024", "Private Party", "Completed", DeepGreen),
        BookingInfo("Goravara Kunitha Sangha", "28 Sep 2024", "Religious Festival", "Completed", DeepGreen)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Recent Bookings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mockBookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(booking.troupeName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(booking.eventType, fontSize = 14.sp, color = Color.Gray)
                            Text(booking.date, fontSize = 12.sp, color = Color.Gray)
                        }
                        Surface(
                            color = booking.statusColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                booking.status,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = booking.statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

data class BookingInfo(val troupeName: String, val date: String, val eventType: String, val status: String, val statusColor: Color)

@Composable
fun SeasonalOffersScreen() {
    val offers = listOf(
        OfferInfo("Dussehra Special", "Flat 20% OFF on all Yakshagana troupes this festive season!", "FESTIVE20", Saffron),
        OfferInfo("Wedding Season Combo", "Book Dollu Kunitha & Pooja Kunitha together and save ₹5,000", "WEDDING5K", TraditionalRed),
        OfferInfo("Early Bird Corporate", "15% OFF for corporate bookings made 30 days in advance", "CORP15", JungleGreen)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Seasonal Offers",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(offers) { offer ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = offer.themeColor.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, offer.themeColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = offer.themeColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(offer.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = offer.themeColor)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(offer.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = offer.themeColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "CODE: ${offer.promoCode}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class OfferInfo(val title: String, val description: String, val promoCode: String, val themeColor: Color)

@Composable
fun SettingsScreen(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Appearance Section
        Text("Appearance", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Theme Mode", fontWeight = FontWeight.Medium)
                Row {
                    FilledTonalButton(
                        onClick = { onThemeChange(false) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (!isDarkTheme) Saffron else Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Light", color = if (!isDarkTheme) Color.Black else MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledTonalButton(
                        onClick = { onThemeChange(true) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isDarkTheme) Color.DarkGray else Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Dark", color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Notifications Section
        Text("Notifications", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Notifications", fontWeight = FontWeight.Medium)
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = DeepGreen, checkedTrackColor = JungleGreen.copy(alpha = 0.5f))
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (notificationsEnabled) "Notifications are currently ON" else "Notifications are currently OFF",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun MyAccountScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "My Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Profile Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Saffron,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Darshan H", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Account Status: ", color = Color.Gray, fontSize = 14.sp)
                Surface(
                    color = DeepGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "ACTIVE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = DeepGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Account Options
        AccountItem(icon = Icons.Default.Favorite, title = "Activity Favourites", subtitle = "View your saved troupes")
        AccountItem(icon = Icons.Default.Lock, title = "Privacy", subtitle = "Manage your data and visibility")
        AccountItem(icon = Icons.Default.Info, title = "Terms of Use", subtitle = "Review our service policies")
        AccountItem(icon = Icons.Default.ExitToApp, title = "Log Out", subtitle = "", textColor = TraditionalRed)

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "App Version 1.0.0",
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun AccountItem(icon: ImageVector, title: String, subtitle: String, textColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Action */ }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if(textColor == TraditionalRed) textColor else MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = if(textColor == TraditionalRed) textColor else MaterialTheme.colorScheme.onBackground)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun PlaceholderScreen(title: String, icon: ImageVector) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text("Coming Soon!", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroupeListScreen(onTroupeClick: (String) -> Unit, onChatClick: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var selectedArtType by remember { mutableStateOf<String?>(null) }
    var selectedDistrict by remember { mutableStateOf<String?>(null) }

    val artTypes = TroupeData.troupes.map { it.artType }.distinct()
    val districts = TroupeData.troupes.map { it.district }.distinct()

    val filteredTroupes = TroupeData.troupes.filter {
        (searchText.isEmpty() || it.name.contains(searchText, ignoreCase = true)) &&
        (selectedArtType == null || it.artType == selectedArtType) &&
        (selectedDistrict == null || it.district == selectedDistrict)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Kalavidara Balaga", 
                        color = Color.White,
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = JungleGreen
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onChatClick,
                containerColor = TraditionalRed,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Face, contentDescription = null) },
                text = { Text("Ask AI Bot") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search Art Form or Group") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                )
            )


            // Art Type Filter
            Text(
                "Filter by Art Type", 
                modifier = Modifier.padding(horizontal = 16.dp), 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(artTypes) { type ->
                    FilterChip(
                        selected = selectedArtType == type,
                        onClick = { selectedArtType = if (selectedArtType == type) null else type },
                        label = { Text(type) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // District Filter
            Text(
                "Filter by District", 
                modifier = Modifier.padding(horizontal = 16.dp), 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(districts) { district ->
                    FilterChip(
                        selected = selectedDistrict == district,
                        onClick = { selectedDistrict = if (selectedDistrict == district) null else district },
                        label = { Text(district) },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // List of Troupes
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredTroupes) { troupe ->
                    TroupeCard(troupe = troupe, onClick = { onTroupeClick(troupe.id) })
                }
            }
        }
    }
}

@Composable
fun TroupeImage(imagePath: String, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Fit) {
    val context = LocalContext.current
    if (imagePath.startsWith("http")) {
        AsyncImage(
            model = imagePath,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
            error = painterResource(id = R.drawable.artist_foreground),
            placeholder = painterResource(id = R.drawable.kb_foreground) // Show a placeholder while loading
        )
    } else {
        val resId = context.resources.getIdentifier(imagePath, "drawable", context.packageName)
        val painter = if (resId != 0) painterResource(id = resId) else painterResource(id = R.drawable.artist_foreground)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

@Composable
fun TroupeCard(troupe: Troupe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier
            .height(250.dp) // Increased from 200.dp
            .background(TealBlue)
        ) {
            TroupeImage(
                imagePath = troupe.images.firstOrNull() ?: "artist_foreground",
                modifier = Modifier.fillMaxSize(), // Removed padding
                contentScale = ContentScale.Crop // Changed to Crop for full coverage
            )
            
            // Verified Badge
            if (troupe.isVerified) {
                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomStart = 12.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = DeepGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verified", color = DeepGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    troupe.name, 
                    color = Color.White, 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = {}, 
                        label = { Text(troupe.artType) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = Color.White,
                            containerColor = Saffron.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(troupe.district, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(troupe.basePrice, color = GoldenYellow, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TroupeDetailScreen(troupe: Troupe, onBack: () -> Unit) {
    val context = LocalContext.current
    var showBookingDialog by remember { mutableStateOf(false) }

    if (showBookingDialog) {
        BookingDialog(troupe = troupe, onDismiss = { showBookingDialog = false })
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = "tel:${troupe.phone}".toUri()
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, DeepGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = DeepGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Inquiry", color = DeepGreen)
                }
                
                Button(
                    onClick = { showBookingDialog = true },
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Book Directly", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Image
            Box(modifier = Modifier
                .height(300.dp)
                .background(TealBlue)
            ) {
                TroupeImage(
                    imagePath = troupe.images.firstOrNull() ?: "artist_foreground",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(16.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    troupe.name, 
                    style = MaterialTheme.typography.headlineMedium, 
                    fontWeight = FontWeight.Bold, 
                    color = if (isSystemInDarkTheme()) Saffron else DarkMaroon
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(onClick = {}, label = { Text(troupe.artType) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(troupe.district, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                }

                // Marketplace Details
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(troupe.basePrice, color = TraditionalRed, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    if (troupe.isVerified) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(16.dp))
                        Text(" Verified Listing", color = DeepGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Availability", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (isSystemInDarkTheme()) Saffron else DarkMaroon)
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    troupe.availability.forEach { day ->
                        Surface(
                            modifier = Modifier.padding(end = 4.dp),
                            color = JungleGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(day, modifier = Modifier.padding(8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("About", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (isSystemInDarkTheme()) Saffron else DarkMaroon)
                Text(troupe.description, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Instruments", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (isSystemInDarkTheme()) Saffron else DarkMaroon)
                troupe.instruments.forEach { instrument ->
                    Text("• $instrument", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, modifier = Modifier.padding(vertical = 2.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Equipment", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (isSystemInDarkTheme()) Saffron else DarkMaroon)
                troupe.equipment.forEach { item ->
                    Text("• $item", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, modifier = Modifier.padding(vertical = 2.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Portfolio Gallery", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (isSystemInDarkTheme()) Saffron else DarkMaroon)
                
                // Using the actual images from the troupe data
                val galleryItems = troupe.images
                
                // Fixed Grid for better stability and performance
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rows = galleryItems.chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { imagePath ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(200.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    TroupeImage(
                                        imagePath = imagePath,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            // Add an invisible spacer if the row only has 1 item
                            if (rowItems.count() == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingDialog(troupe: Troupe, onDismiss: () -> Unit) {
    var selectedEvent by remember { mutableStateOf("Wedding") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Direct Booking: ${troupe.name}") },
        text = {
            Column {
                Text("Select Event Type:", fontWeight = FontWeight.Bold)
                val events = listOf("Wedding", "Private Party", "Corporate Event", "Religious Festival")
                events.forEach { event ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEvent = event }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(selected = (selectedEvent == event), onClick = { selectedEvent = event })
                        Text(event, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Estimated Price: ${troupe.basePrice}", fontWeight = FontWeight.Bold, color = TraditionalRed)
                Text("Available: ${troupe.availability.joinToString(", ")}", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Note: Our verified manager will contact you within 2 hours to finalize the schedule.", fontSize = 12.sp, color = Color.Gray)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
            ) {
                Text("Confirm Booking Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
