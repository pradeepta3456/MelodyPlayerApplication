package com.example.musicplayerapplication.View

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val screen = intent.getStringExtra("screen") ?: "about"
            when (screen) {
                "about" -> AboutScreen()
                "terms" -> TermsScreen()
                "privacy" -> PrivacyPolicyScreen()
                else -> AboutScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E2E),
                        Color(0xFF0A0A0F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        "About",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // App Icon
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = "App Icon",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "MelodyPlayer",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    "Version 1.0.0",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "About MelodyPlayer",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "MelodyPlayer is a modern music streaming application that brings " +
                            "your favorite songs right to your fingertips. Upload, organize, " +
                            "and enjoy your music collection with an intuitive interface and " +
                            "powerful features.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(color = Color(0xFF4A4A5E))

                        Spacer(modifier = Modifier.height(16.dp))

                        AboutInfoRow(Icons.Default.Code, "Developed with", "Jetpack Compose")
                        AboutInfoRow(Icons.Default.Cloud, "Powered by", "Firebase & Cloudinary")
                        AboutInfoRow(Icons.Default.Security, "Secure", "End-to-end encryption")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2A3E)
                    )
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        AboutActionItem(
                            icon = Icons.Default.BugReport,
                            title = "Report a Bug",
                            onClick = {
                                // Open email or bug report form
                            }
                        )

                        AboutActionItem(
                            icon = Icons.Default.Star,
                            title = "Rate this App",
                            onClick = {
                                // Open Play Store
                            }
                        )

                        AboutActionItem(
                            icon = Icons.Default.Share,
                            title = "Share App",
                            onClick = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out MelodyPlayer!")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Made with ❤️ by the MelodyPlayer Team",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Text(
                    "© 2026 MelodyPlayer. All rights reserved.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AboutInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            "$label: ",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            value,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AboutActionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = Color(0xFF8B5CF6),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Go",
            tint = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen() {
    GenericTextScreen(
        title = "Terms of Service",
        content = """
            1. Acceptance of Terms
            By accessing and using MelodyPlayer, you accept and agree to be bound by the terms and provision of this agreement.

            2. Use License
            Permission is granted to temporarily download one copy of the materials on MelodyPlayer for personal, non-commercial transitory viewing only.

            3. User Content
            You retain all rights to any content you submit, post or display on or through MelodyPlayer. By submitting content, you grant us a worldwide, non-exclusive, royalty-free license to use, copy, reproduce, process, adapt, modify, publish, transmit, display and distribute such content.

            4. Prohibited Uses
            You may not use MelodyPlayer:
            • For any unlawful purpose
            • To solicit others to perform or participate in any unlawful acts
            • To violate any international, federal, provincial or state regulations, rules, laws, or local ordinances
            • To infringe upon or violate our intellectual property rights or the intellectual property rights of others

            5. Disclaimer
            The materials on MelodyPlayer are provided on an 'as is' basis. MelodyPlayer makes no warranties, expressed or implied, and hereby disclaims and negates all other warranties including, without limitation, implied warranties or conditions of merchantability, fitness for a particular purpose, or non-infringement of intellectual property or other violation of rights.

            6. Limitations
            In no event shall MelodyPlayer or its suppliers be liable for any damages arising out of the use or inability to use MelodyPlayer.

            7. Changes to Terms
            MelodyPlayer may revise these terms of service at any time without notice. By using this application you are agreeing to be bound by the then current version of these terms of service.

            Last updated: January 21, 2026
        """.trimIndent()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen() {
    GenericTextScreen(
        title = "Privacy Policy",
        content = """
            Your privacy is important to us. This Privacy Policy explains how MelodyPlayer collects, uses, and protects your personal information.

            1. Information We Collect
            • Account information (email, display name)
            • Music listening preferences and history
            • Device information
            • Usage statistics

            2. How We Use Your Information
            • To provide and maintain our service
            • To notify you about changes to our service
            • To provide customer support
            • To gather analysis or valuable information to improve our service
            • To monitor the usage of our service

            3. Data Storage
            Your data is stored securely using Firebase and Cloudinary services. We implement appropriate security measures to protect against unauthorized access, alteration, disclosure or destruction of your personal information.

            4. Sharing Your Information
            We do not sell, trade, or rent your personal identification information to others. We may share generic aggregated demographic information not linked to any personal identification information.

            5. Your Rights
            You have the right to:
            • Access your personal data
            • Correct inaccurate data
            • Request deletion of your data
            • Object to our processing of your data
            • Request data portability

            6. Cookies and Tracking
            We use cookies and similar tracking technologies to track activity on our service and hold certain information to improve and analyze our service.

            7. Third-Party Services
            We may employ third-party companies and individuals to facilitate our service, provide the service on our behalf, or assist us in analyzing how our service is used.

            8. Children's Privacy
            Our service does not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13.

            9. Changes to This Privacy Policy
            We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page.

            10. Contact Us
            If you have any questions about this Privacy Policy, please contact us.

            Last updated: January 21, 2026
        """.trimIndent()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericTextScreen(title: String, content: String) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E2E),
                        Color(0xFF0A0A0F)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A3E)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        content,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
