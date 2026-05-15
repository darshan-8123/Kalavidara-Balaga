package com.example.kalavidarabalaga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalavidarabalaga.ui.theme.DarkMaroon
import com.example.kalavidarabalaga.ui.theme.DeepGreen
import com.example.kalavidarabalaga.ui.theme.JungleGreen
import com.example.kalavidarabalaga.ui.theme.Saffron

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(onBack: () -> Unit) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(
        ChatMessage("Namaste! I am Kalavidara Sahayaka. How can I help you find the perfect troupe today?", false)
    ) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Assistant", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask about troupes, prices, or art forms...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val userQuery = inputText
                                messages.add(ChatMessage(userQuery, true))
                                inputText = ""
                                // Simulate AI Response
                                val response = generateAiResponse(userQuery)
                                messages.add(ChatMessage(response, false))
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (message.isUser) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 16.dp
            ),
            tonalElevation = 2.dp,
            shadowElevation = 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (message.isUser) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
        }
    }
}

fun generateAiResponse(query: String): String {
    val q = query.lowercase()
    return when {
        q.contains("yakshagana") -> "Coastal Yakshagana Mandali from Udupi is our top-rated troupe. They specialize in mythological plays and charge around ₹25,000 per performance."
        q.contains("dollu") -> "Sri Vinayaka Dollu Kunitha from Mandya is highly recommended. They have 15 years of experience and are verified by our platform!"
        q.contains("price") || q.contains("cost") -> "Troupes start from ₹10,000 (Goravara Kunitha) and go up to ₹25,000 for elaborate forms like Yakshagana. You can book them directly through our marketplace."
        q.contains("wedding") || q.contains("party") -> "For weddings, Pooja Kunitha or Dollu Kunitha are very popular for the grand welcome. Would you like me to show you available troupes in Mandya or Mysuru?"
        q.contains("verified") -> "Verified troupes have been personally vetted by Kalavidara Balaga. They offer guaranteed availability and professional management."
        q.contains("hi") || q.contains("hello") -> "Namaste! How can I assist you with your cultural event planning today?"
        else -> "That's an interesting question about our rich Karnataka heritage! Based on your query about '$query', I recommend checking our 'Verified Marketplace' section for the best troupes currently available for booking."
    }
}
