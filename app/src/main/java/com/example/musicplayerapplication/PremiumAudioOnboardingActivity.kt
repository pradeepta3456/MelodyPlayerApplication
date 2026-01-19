package com.example.musicplayerapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

class PremiumAudioOnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PremiumAudioOnboardingScreen(
                onNextClick = { /* Navigate to next screen */ },
                onSkipClick = { /* Skip onboarding */ }
            )
        }
    }
}
@Composable
fun PremiumAudioOnboardingScreen(
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
            // Headphone Icon
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = (-4).dp)
                        .border(3.dp, Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                )

                Box(
                    modifier = Modifier
                        .size(width = 12.dp, height = 20.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = 6.dp, y = (-2).dp)
                        .border(2.dp, Color.White, RoundedCornerShape(6.dp))
                )
                // Right ear cup
                Box(
                    modifier = Modifier
                        .size(width = 12.dp, height = 20.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-6).dp, y = (-2).dp)
                        .border(2.dp, Color.White, RoundedCornerShape(6.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            Text(
                text = "Premium Audio Quality",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = "Experience music with customizable equalizer and crossfade",
                color = WhiteSoft,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .border(1.dp, WhiteSoft, CircleShape)
                )
                // Second dot - pill (active)
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(8.dp)
                        .background(WhiteSoft, RoundedCornerShape(4.dp))
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .border(1.dp, WhiteSoft, CircleShape)
                )
            }
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleButton
                )
            ) {
                Text(
                    text = "Next",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Skip Text
            Text(
                text = "Skip",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onSkipClick() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PremiumAudioOnboardingScreenPreview() {
    PremiumAudioOnboardingScreen()
}
