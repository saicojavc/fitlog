package com.saico.feature.dashboard.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.UnitsConfig
import com.saico.core.model.UserProfile
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.EmeraldGreen
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    uiState: DashboardUiState,
    onLoginWithGoogle: (String) -> Unit,
    onLogout: () -> Unit,
    onUpdateProfile: (UserProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)
    val googleWebClientId =
        "952192663642-p6hsjk1374qp0aehrb04jc6l501nqoor.apps.googleusercontent.com"

    fun onGoogleSignIn() {
        scope.launch {
            try {
                val googleIdOption =
                    GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
                        .setServerClientId(googleWebClientId).build()
                val request =
                    GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
                val result = credentialManager.getCredential(context, request)
                val googleIdToken =
                    GoogleIdTokenCredential.createFrom(result.credential.data).idToken
                onLoginWithGoogle(googleIdToken)
            } catch (e: Exception) {
                android.util.Log.e("Auth", "Error: ${e.message}")
            }
        }
    }

    ProfileContent(
        uiState = uiState,
        onSave = onUpdateProfile,
        onLoginClick = { onGoogleSignIn() },
        onLogoutClick = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    uiState: DashboardUiState,
    onSave: (UserProfile) -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val profile = uiState.userProfile ?: UserProfile(0, 0.0, 0.0, "", 0, 0)
    val units = uiState.userData?.unitsConfig ?: UnitsConfig.METRIC

    var age by remember(profile) { mutableStateOf(profile.age.toString()) }
    var weight by remember(profile, units) {
        val displayWeight =
            if (units == UnitsConfig.METRIC) profile.weightKg else UnitsConverter.kgToLb(profile.weightKg)
        mutableStateOf("%.1f".format(displayWeight).replace(",", "."))
    }
    var heightCm by remember(profile) {
        mutableStateOf(
            "%.1f".format(profile.heightCm).replace(",", ".")
        )
    }
    var heightFt by remember(profile) { mutableStateOf(UnitsConverter.cmToFtIn(profile.heightCm).first.toString()) }
    var heightIn by remember(profile) { mutableStateOf(UnitsConverter.cmToFtIn(profile.heightCm).second.toString()) }
    var stepsGoal by remember(profile) { mutableStateOf(profile.dailyStepsGoal.toString()) }
    var caloriesGoal by remember(profile) { mutableStateOf(profile.dailyCaloriesGoal.toString()) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF1E293B))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
            content = {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = FitlogIcons.Save,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    FitlogText(
                        text = stringResource(R.string.update_profile_title).uppercase(),
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = {
                            val wVal = weight.toDoubleOrNull() ?: profile.weightKg
                            val wKg =
                                if (units == UnitsConfig.METRIC) wVal else UnitsConverter.lbToKg(
                                    wVal
                                )
                            val hVal = if (units == UnitsConfig.METRIC) heightCm.toDoubleOrNull()
                                ?: profile.heightCm else UnitsConverter.ftInToCm(
                                heightFt.toIntOrNull() ?: 0, heightIn.toIntOrNull() ?: 0
                            )
                            onSave(
                                profile.copy(
                                    age = age.toIntOrNull() ?: profile.age,
                                    weightKg = wKg,
                                    heightCm = hVal,
                                    dailyStepsGoal = stepsGoal.toIntOrNull()
                                        ?: profile.dailyStepsGoal,
                                    dailyCaloriesGoal = caloriesGoal.toIntOrNull()
                                        ?: profile.dailyCaloriesGoal
                                )
                            )
                            showConfirmDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        FitlogText(
                            text = stringResource(R.string.accept).uppercase(),
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PaddingDim.MEDIUM),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingDim.MEDIUM)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(2.dp, Color(0xFF10B981), CircleShape)
                .padding(4.dp), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = FitlogIcons.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        uiState.authUser?.let { user ->
            FitlogText(
                text = user.displayName ?: user.email ?: "Usuario",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextButton(onClick = onLogoutClick) {
                FitlogText(
                    text = stringResource(R.string.unlink_account), color = Color.Red.copy(alpha = 0.7f)
                )
            }
        } ?: run {
            FitlogText(
                text = stringResource(R.string.your_profile),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            FitlogLoginButton(onClick = onLoginClick, isLoading = uiState.isLoadingLogin)
        }

        LevelCard(levelText = profile.level)

        ProfileSectionCard(title = stringResource(R.string.personal_info)) {
            EditableInfoRow(
                icon = FitlogIcons.Cake,
                label = stringResource(R.string.age),
                value = age,
                onValueChange = { age = it })
            EditableInfoRow(
                icon = FitlogIcons.Weight,
                label = if (units == UnitsConfig.METRIC) "kg" else "lb",
                value = weight,
                onValueChange = { weight = it })
            if (units == UnitsConfig.METRIC) {
                EditableInfoRow(
                    icon = FitlogIcons.Height,
                    label = "cm",
                    value = heightCm,
                    onValueChange = { heightCm = it })
            } else {
                HeightImperialRow(heightFt, heightIn, { heightFt = it }, { heightIn = it })
            }
        }

        ProfileSectionCard(title = stringResource(R.string.daily_goals)) {
            EditableInfoRow(
                icon = FitlogIcons.Walk,
                label = stringResource(id = R.string.daily_steps),
                value = stepsGoal,
                onValueChange = { stepsGoal = it })
            EditableInfoRow(
                icon = FitlogIcons.Fire,
                label = stringResource(id = R.string.calories_to_burn),
                value = caloriesGoal,
                onValueChange = { caloriesGoal = it })
        }

        Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))
        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
        ) {
            FitlogText(
                text = stringResource(R.string.save_and_continue),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun HeightImperialRow(
    ft: String, inc: String, onFtChange: (String) -> Unit, onInChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = FitlogIcons.Height,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FitlogText(text = stringResource(id = R.string.height), color = Color(0xFF94A3B8))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = ft,
                onValueChange = onFtChange,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    fontSize = 16.sp
                ),
                modifier = Modifier.width(30.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(Color(0xFF10B981))
            )
            FitlogText(text = " ft ", color = Color.White)
            BasicTextField(
                value = inc,
                onValueChange = onInChange,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    fontSize = 16.sp
                ),
                modifier = Modifier.width(30.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(Color(0xFF10B981))
            )
            FitlogText(text = " in", color = Color.White)
        }
    }
}

@Composable
fun FitlogLoginButton(onClick: () -> Unit, isLoading: Boolean) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(8.dp, CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            EmeraldGreen, Color(0xFF059669)
                        )
                    )
                ), contentAlignment = Alignment.Center
        ) {
            if (isLoading) CircularProgressIndicator(
                color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp
            )
            else Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = FitlogIcons.Google,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.backup_with_google),
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

@Composable
fun LevelCard(levelText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = FitlogIcons.Star,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FitlogText(
                    text = stringResource(R.string.your_main_goal), color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FitlogText(
                    text = stringResource(R.string.running),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)
                ) {
                    FitlogText(
                        text = stringResource(R.string.times_a_week),
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.White.copy(alpha = 0.05f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FitlogText(text = stringResource(R.string.level), color = Color(0xFF94A3B8))
                FitlogText(
                    text = levelText.uppercase(),
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun EditableInfoRow(
    icon: ImageVector, label: String, value: String, onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FitlogText(text = label, color = Color(0xFF94A3B8))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                fontSize = 16.sp
            ),
            modifier = Modifier.width(100.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = SolidColor(Color(0xFF10B981))
        )
    }
}

@Composable
fun ProfileSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FitlogText(text = title, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
