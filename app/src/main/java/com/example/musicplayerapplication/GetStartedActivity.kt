package com.example.musicplayerapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayerapplication.ui.theme.PurpleBackground
import com.example.musicplayerapplication.ui.theme.PurpleButton
import com.example.musicplayerapplication.ui.theme.WhiteSoft

class GetStartedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GetStartedOnboardingScreen(
                onNextClick = { /* Navigate to next screen */ },
                onSkipClick = { /* Skip onboarding */ }
            )
        }
    }
}

@Composable
fun GetStartedOnboardingScreen(
    onNextClick: () -> Unit = {},
    onSkipClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Music Speaker Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Inner speaker circle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Stream music",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Ai generated playlist based on your mood and habits",
                color = WhiteSoft,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First dot - filled
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.White, CircleShape)
                )
                // Second dot - hollow
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .border(1.dp, WhiteSoft, CircleShape)
                )
                // Third dot - line/pill shape (active indicator style)
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(8.dp)
                        .background(WhiteSoft, RoundedCornerShape(4.dp))
                )
            }
        }

        // Get Started Button at bottom
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleButton
            )
        ) {
            Text(
                text = "Get started",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun melodyplayPreview() {
    GetStartedOnboardingScreen(
        onNextClick = { },
        onSkipClick = { }
    )
}
