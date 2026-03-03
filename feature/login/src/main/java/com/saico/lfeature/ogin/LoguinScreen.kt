package com.saico.lfeature.ogin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.GravityParticlesBackground
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.navigation.routes.dashboard.DashboardRoute
import com.saico.core.ui.navigation.routes.onboarding.OnboardingRoute
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.techBlue
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val googleWebClientId =
        "952192663642-p6hsjk1374qp0aehrb04jc6l501nqoor.apps.googleusercontent.com"


// ... dentro de la UI
    if (error != null) {    FitlogText(
        text = error!!.asString(),
        color = Color.Red.copy(alpha = 0.8f),
        style = MaterialTheme.typography.bodySmall
    )
    }

    fun onGoogleSignIn() {
        scope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(googleWebClientId)
                    .build()
                val request =
                    GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
                val result = credentialManager.getCredential(context, request)
                val googleIdToken =
                    GoogleIdTokenCredential.createFrom(result.credential.data).idToken

                viewModel.loginWithGoogle(googleIdToken) {
                    // Al tener éxito, navegamos al Dashboard (ajusta la ruta según tu navegación real)
                    navController.navigate(DashboardRoute.DashboardScreenRoute.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GravityParticlesBackground() // El fondo que unifica todo el rediseño

        Content(
            navController = navController,
            isLoading = isLoading,
            error = error,
            onRestoreClick = { onGoogleSignIn() }
        )
    }
}

@Composable
fun Content(
    navController: NavHostController,
    isLoading: Boolean,
    error: UiText?,
    onRestoreClick: () -> Unit
) {
    val context = LocalContext.current

    val versionName = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) { "1.0.0" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- LOGO CON GLOW ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 100.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .shadow(30.dp, CircleShape, spotColor = techBlue) // Brillo exterior
                    .background(Color(0xFF0D1424).copy(alpha = 0.4f), CircleShape)
                    .border(2.dp, Brush.linearGradient(listOf(techBlue, Color.Transparent)), CircleShape)
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(110.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // --- BIENVENIDA ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FitlogText(
                text = stringResource(id = R.string.welcome_to_fitlog).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            SpacerHeight(PaddingDim.MEDIUM)
            FitlogText(
                text = stringResource(id = R.string.login_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        // --- BOTONES DE ACCIÓN ---
        Column(
            modifier = Modifier.padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón "GET STARTED" (Principal con Degradado)
            Button(
                onClick = { navController.navigate(OnboardingRoute.OnboardingScreenRoute.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(15.dp, CircleShape, spotColor = techBlue),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(techBlue, Color(0xFF216EE0))))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FitlogText(
                            text = stringResource(id = R.string.get_started).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            SpacerHeight(PaddingDim.MEDIUM)

            // Botón de Restaurar / Tengo Cuenta (Estilo Glassmorphism)
            OutlinedButton(
                onClick = onRestoreClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.05f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = techBlue, strokeWidth = 2.dp)
                } else {
                    FitlogText(
                        text = stringResource(R.string.have_account).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    )
                }
            }

            // Muestreo de Error (Respetando el stringResource)
            if (error != null) {
                SpacerHeight(PaddingDim.MEDIUM)
                Surface(
                    color = Color(0xFFFF4550).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF4550).copy(alpha = 0.3f))
                ) {
                    FitlogText(
                        text = error.asString(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color(0xFFFF4550),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            SpacerHeight(PaddingDim.EXTRA_LARGE)

            // Footer con Versión y Slogan
            FitlogText(
                text = "v$versionName • ${stringResource(id = R.string.app_slogan).uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.2f),
                letterSpacing = 1.sp
            )
        }
    }
}