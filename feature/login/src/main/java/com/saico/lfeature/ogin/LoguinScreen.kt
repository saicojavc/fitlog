package com.saico.lfeature.ogin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.dashboard.DashboardRoute
import com.saico.core.ui.navigation.routes.onboarding.OnboardingRoute
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.PaddingDim
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
    val googleWebClientId = "952192663642-p6hsjk1374qp0aehrb04jc6l501nqoor.apps.googleusercontent.com"

    fun onGoogleSignIn() {
        scope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(googleWebClientId)
                    .build()
                val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
                val result = credentialManager.getCredential(context, request)
                val googleIdToken = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
                
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

    Content(
        navController = navController,
        isLoading = isLoading,
        error = error,
        onRestoreClick = { onGoogleSignIn() }
    )
}

@Composable
fun Content(
    navController: NavHostController,
    isLoading: Boolean,
    error: String?,
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
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(GradientColors)).padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 80.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp).background(Color.White.copy(alpha = 0.05f), CircleShape).border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)) {
                Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo", modifier = Modifier.size(100.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FitlogText(text = stringResource(id = R.string.welcome_to_fitlog).uppercase(), style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp), textAlign = TextAlign.Center, color = Color.White)
            SpacerHeight(PaddingDim.MEDIUM)
            FitlogText(text = stringResource(id = R.string.login_subtitle), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = Color.White.copy(alpha = 0.7f))
        }

        Column(modifier = Modifier.padding(bottom = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { navController.navigate(OnboardingRoute.OnboardingScreenRoute.route) },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.get_started).uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(12.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }

            SpacerHeight(PaddingDim.MEDIUM)

            // BOTÓN DE RESTAURAR CUENTA
            TextButton(
                onClick = onRestoreClick,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    FitlogText(text = "¿YA TIENES CUENTA SINCRONIZADA?", fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
                }
            }

            if (error != null) {
                SpacerHeight(PaddingDim.SMALL)
                FitlogText(text = error, color = Color.Red.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            }

            SpacerHeight(PaddingDim.LARGE)

            FitlogText(text = "v$versionName • ${stringResource(id = R.string.app_slogan).uppercase()}", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f))
        }
    }
}
