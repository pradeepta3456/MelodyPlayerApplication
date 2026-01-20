package com.example.musicplayerapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicplayerapplication.ui.theme.MusicPlayerApplicationTheme


class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerApplicationTheme  () {
                SignInBody()
            }
        }
    }
}

@Composable
fun SignInBody() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    // Function to navigate to Dashboard
    fun navigateToDashboard(userEmail: String, userPassword: String) {
        val intent = Intent(context, DashboardActivity::class.java)
        intent.putExtra("email", userEmail)
        intent.putExtra("password", userPassword)
        context.startActivity(intent)
        activity?.finish()
    }

    // Function to validate email and password
    fun validateAndSignIn() {
        when {
            email.isEmpty() -> {
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }

            password.isEmpty() -> {
                Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
            }

            password.length < 6 -> {
                Toast.makeText(
                    context,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                // Successful validation
                navigateToDashboard(email, password)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8B5CF6),
                        Color(0xFF7C3AED)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Music Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = Color(0xFF9333EA),
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_music_note_24),
                    contentDescription = "Music Note",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Melody Play",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Email Field
                    Text(
                        text = "E-mail",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Your@gmail.com", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_email_24),
                                contentDescription = "Email",
                                tint = Color.Gray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF334155),
                            unfocusedContainerColor = Color(0xFF334155),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Password",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••••••••", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_lock_24),
                                contentDescription = "Lock",
                                tint = Color.Gray
                            )
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        if (passwordVisible) R.drawable.baseline_visibility_off_24
                                        else R.drawable.baseline_visibility_24
                                    ),
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF334155),
                            unfocusedContainerColor = Color(0xFF334155),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Forgot Password
                    TextButton(
                        onClick = {
                            val intent = Intent(context, ForgotPasswordActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Forgot password?",
                            color = Color(0xFF8B5CF6),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sign In Button
                    Button(
                        onClick = { validateAndSignIn() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5CF6)
                        )
                    ) {
                        Text(
                            text = "Sign in",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // OR Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "or",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Continue with Google Button
                    OutlinedButton(
                        onClick = {
                            // Simulate Google Sign-In by using a demo Google account
                            // In a real app, you would integrate Google Sign-In SDK
                            val googleEmail = "user@gmail.com"
                            val googlePassword =
                                "google_auth_token" // This would be a token in real implementation

                            Toast.makeText(context, "Signing in with Google...", Toast.LENGTH_SHORT)
                                .show()

                            // Navigate to dashboard with Google credentials
                            navigateToDashboard(googleEmail, googlePassword)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White
                        ),
                        border = null
                    ) {
                        Image(
                            painter = painterResource(R.drawable.img_5),
                            contentDescription = "Google",
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
